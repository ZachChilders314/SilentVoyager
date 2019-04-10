package com.x10host.burghporter31415.silentvoyager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.util.ArrayUtils;
import com.x10host.burghporter31415.fragments.ConnectionsFragment;
import com.x10host.burghporter31415.fragments.DashboardPagerAdapter;
import com.x10host.burghporter31415.fragments.FragmentUtils;
import com.x10host.burghporter31415.fragments.RequestsFragment;
import com.x10host.burghporter31415.internetservices.BackgroundServiceBroadcast;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Dashboard extends AppCompatActivity implements RequestsFragment.OnConnectionAddedListener, ConnectionsFragment.OnConnectionRemovedListener {

    private VideoView introVideoView;

    private ViewPager viewPager;
    private DashboardPagerAdapter adapter;
    private Button btnFilterOptions;
    private Button btnAddConnection;

    private final String FILE_NAME="Silent_Voyager_Credentials.txt";
    private String currentUsername;

    private FilterUtils utilsSaved;

    private HashMap<String, String[]> userHash = new HashMap<String, String[]>();
    private int[] last_ids = new int[3];

    private String[] arrResults = null;
    private String[] connectionResults = null;
    private String[] requestResults = null;
    private String[] receivedResults = null;

    private final int MAX_SIZE = 500;

    final FormPost<String, String> resultFormPost = new FormPost<>();
    final PHPPage resultRequestPage = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/get_results.php");
    final PHPPage resultRequestPageResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/connection_results.php");
    final PHPPage resultRequestPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/request_connection_results.php");
    final PHPPage resultReceivedPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/received_connection_results.php");
    final PHPPage checkUpdateResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/check_update.php");
    final PHPPage deleteActionResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/delete_actions.php");

    /*Need to cancel handler when activity is not active*/

    final Handler handler = new Handler();
    final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            http://burghporter31415.x10host.com/Silent_Voyager/App_Scripts/check_update.php?username=burghporter314&password=Ipconfig314&num_0=3&num_1=4&num_2=6

            if(!currentUsername.equals( getIntent().getExtras().getString("username"))) {
                resultFormPost.addPair("friend", currentUsername);
            }

            if(!arrResults[0].isEmpty()) {
                resultFormPost.addPair("recentDateStamp", Dashboard.this.arrResults[0].split(",")[5]);
            } else {
                resultFormPost.addPair("recentDateStamp", "0");
            }

            resultFormPost.addPair("num_0", String.valueOf(last_ids[0]));
            resultFormPost.addPair("num_1", String.valueOf(last_ids[1]));
            resultFormPost.addPair("num_2", String.valueOf(last_ids[2]));

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    final String json = resultFormPost.submitPost(checkUpdateResults, MethodType.POST);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parseUpdate(json);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Repeat code again after 20 seconds
            handler.postDelayed(this, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        utilsSaved = new FilterUtils(getIntent().getExtras().getString("username"), MAX_SIZE, null, null);

        if(currentUsername == null) currentUsername = getIntent().getExtras().getString("username");
        btnFilterOptions = (Button) findViewById(R.id.btnFilterOptions);

        btnFilterOptions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Filter.class);

                /*Need the username for the Filter activity to query the database for common users*/
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                intent.putExtra("password", getIntent().getExtras().getString("password"));

                intent.putExtra("PATH", getIntent().getExtras().getString("PATH"));
                intent.putExtra("connections", connectionResults);

                startActivityForResult(intent, 100);

            }
        });

        btnAddConnection = (Button) findViewById(R.id.btnAddConnection);

        btnAddConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ConnectionAdd.class);

                /*Need the username for the Filter activity to query the database for common users*/
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                intent.putExtra("password", getIntent().getExtras().getString("password"));

                intent.putExtra("PATH", getIntent().getExtras().getString("PATH"));
                intent.putExtra("connections", connectionResults);

                startActivityForResult(intent, 200);

            }
        });

        viewPager = (ViewPager) findViewById(R.id.dashboardViewpager);

        adapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.dashboardTab);
        tabLayout.setupWithViewPager(viewPager);

        com.x10host.burghporter31415.fileactions.File file = new com.x10host.burghporter31415.fileactions.File(FILE_NAME);

        if (file.fileExists(getApplicationContext())) {
            file.deleteFile(getApplicationContext(), FILE_NAME);
        }

        String[] args = {
                getIntent().getExtras().getString("username"),
                getIntent().getExtras().getString("password")
        };

        /*Populate the bundle to pass to individual fragments*/
        Bundle bundle = new Bundle();

        bundle.putString("username", getIntent().getExtras().getString("username"));
        bundle.putString("password", getIntent().getExtras().getString("password"));
        bundle.putString("PATH", getIntent().getExtras().getString("PATH"));

        adapter.setBundle(bundle);

        file.writeToFile(getApplicationContext(), args);

        /*Starts Background Service by sending an Android Broadcast*/
        Intent broadcast = new Intent("com.x10host.burghporter31415.internetservices.android.action.broadcast");
        broadcast.setClass(this, BackgroundServiceBroadcast.class);
        sendBroadcast(broadcast);

        /*The Dashboard should be the handler of the city, coordinates, and map fragment data*/
        resultFormPost.addPair("username", getIntent().getExtras().getString("username"));
        resultFormPost.addPair("password", getIntent().getExtras().getString("password"));

        try {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    String[] results = resultFormPost.submitPost(resultRequestPage, MethodType.POST).split("\n");
                    JSONHolder connectionResultsJSON = null, requestResultsJSON = null, receivedResultsJSON = null;
                    try {
                        connectionResultsJSON = new JSONHolder(new JSONObject(resultFormPost.submitPost(resultRequestPageResults, MethodType.POST)));
                        requestResultsJSON = new JSONHolder(new JSONObject(resultFormPost.submitPost(resultRequestPageRequestResults, MethodType.POST)));
                        receivedResultsJSON = new JSONHolder(new JSONObject(resultFormPost.submitPost(resultReceivedPageRequestResults, MethodType.POST)));
                    } catch(Exception e) {
                        //TODO
                    }

                    userHash.put(getIntent().getExtras().getString("username"), results);
                    try {
                        updateResultSet(results, connectionResultsJSON.getAssocArray(),requestResultsJSON.getAssocArray(), receivedResultsJSON.getAssocArray());
                        populateComponents(results, connectionResultsJSON.getAssocArray(),requestResultsJSON.getAssocArray(), receivedResultsJSON.getAssocArray());

                        last_ids[0] = connectionResultsJSON.getLastId();
                        last_ids[1] = requestResultsJSON.getLastId();
                        last_ids[2] = receivedResultsJSON.getLastId();

                    } catch (JSONException e) {Log.i("com.x10host", e.toString());}
                }
            });

            thread.start();
            thread.join();

            /*TIMER FOR EVERY 10 SECONDS TO RETURN UPDATED PAYLOAD*/
            // Create the Handler object (on the main thread by default)
            // Define the code block to be executed

            /*Remove any handlers that are potentially running during the start of the activity*/
            handler.removeCallbacks(runnableCode);
            handler.removeCallbacksAndMessages(null);

            /*Start handler since activity is loaded*/
            handler.post(runnableCode);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*Callback function after results have been returned for current user*/
    private void populateComponents(String[] results, String[] connectionResults, String[] requestResults, String[] receivedResults) {

        adapter.setData(results, connectionResults, requestResults, receivedResults);
        adapter.notifyDataSetChanged();

    }

    private void updateResultSet(String[] arrResults, String[] connectionResults, String[] requestResults, String[] receivedResults) {
        /*{Location data, connection data, and request data}*/
        this.arrResults = arrResults;
        this.connectionResults = connectionResults;
        this.requestResults = requestResults;
        this.receivedResults = receivedResults;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Bundle savedBundle;

        /*The request and result codes need to be set accordingly in the activities*/
        if (requestCode == 100 && resultCode==200) {

            savedBundle = data.getExtras();

            /*Parse the Data Passed (usernameSelection, maxEntriesSelection, startDate, endDate)*/
            final String username = savedBundle.getString("usernameSelection");
            final String maxEntries = savedBundle.getString("maxEntriesSelection");

            final String startDate = savedBundle.getString("startDate");
            final String endDate = savedBundle.getString("endDate");

            /******************************************DONE IN THREAD AND THREAD MAIN**********************************************/

            if(!currentUsername.equals(username)) {
                if(!userHash.containsKey(username)) {

                    /*We do not have the results stored on the device, so request it*/
                    try {

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                //TODO Put on timer
                                resultFormPost.removePair("friend");
                                resultFormPost.addPair("friend", username); //Extra field in POST body to get results of friend, not current user.

                                final String[] results = resultFormPost.submitPost(resultRequestPage, MethodType.POST).split("\n");
                                final FilterUtils utils = new FilterUtils(username, Integer.parseInt(maxEntries), startDate, endDate);
                                utilsSaved = utils;

                                userHash.put(username, results);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateResultSet(userHash.get(username), connectionResults, requestResults, receivedResults);
                                        String[] resultsFiltered = utils.getArrResult(userHash.get(username));
                                        populateComponents(resultsFiltered, connectionResults, requestResults, receivedResults);
                                    }
                                });

                            }
                        });

                        thread.start();
                        thread.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {

                    /*We already have the data stored, so update the result set without requesting it from server again*/
                    final FilterUtils utils = new FilterUtils(username, Integer.parseInt(maxEntries), startDate, endDate);
                    utilsSaved = utils;

                    updateResultSet(userHash.get(username), connectionResults, requestResults, receivedResults);
                    populateComponents(utilsSaved.getArrResult(userHash.get(username)), connectionResults, requestResults, receivedResults);

                }
                currentUsername = username; /*A different user has been selected, collect the data and insert into hash*/

            } else {

                final String[] arrResults = this.arrResults;

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        final FilterUtils utils = new FilterUtils(username, Integer.parseInt(maxEntries), startDate, endDate);
                        utilsSaved = utils;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String[] results = utils.getArrResult(arrResults);
                                populateComponents(results, connectionResults, requestResults, receivedResults);

                            }
                        });

                    }
                });

                try {
                    thread.start();
                    thread.join(); /*Wait for the thread to finish, and then create executor service to split tasks*/
                } catch (InterruptedException e) {
                }
            }
            /**********************************************************************************************************************/

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private class FilterUtils {

        private String username;
        private int maxEntries;
        private String beginDate;
        private String endDate;

        public FilterUtils(String username, int maxEntries, String beginDate, String endDate) {
            this.username = username;
            this.maxEntries = maxEntries;
            this.beginDate = beginDate;
            this.endDate = endDate;
        }

        public FilterUtils() {}

        public void setUsername(String username) {this.username = username;}
        public void setMaxEntries(int maxEntries) {this.maxEntries = maxEntries;}
        public void setBeginDate(String beginDate) {this.beginDate = beginDate;}
        public void setEndDate(String endDate) {this.endDate = endDate;}

        public String getUsername() {return this.username;}
        public int getMaxEntries() {return this.maxEntries;}
        public String getBeginDate() {return this.beginDate;}
        public String getEndDate() {return this.endDate;}

        public boolean isValidEntry(Pair pair) {

            if(this.beginDate == null || this.endDate == null) {return this.username.equals(pair.username);}

            return (this.username.equals(pair.username)
                    && (this.beginDate.compareTo(pair.date) < 0)
                    && (this.endDate.compareTo(pair.date) > 0));

        }

        public String[] getArrResult(String[] arr) {

            if(arr == null || arr.length == 0 || arr[0].isEmpty()) { return null; }

            //Row contains: Username, lat, long, alt, City, Datestamp
            ArrayList<String> list = new ArrayList<String>();

            int itemPosNum = 0;
            for(int i = 0; i < arr.length && itemPosNum < this.maxEntries; i++) {

                String[] items = arr[i].split(",");

                if(isValidEntry(new Pair(items[0], items[5]))) {
                    list.add(arr[i]);
                    itemPosNum++;
                }

            }
            String[] arrElems = new String[list.size()];
            return list.toArray(arrElems);
        }


        private class Pair {
            private String username;
            private String date;

            public Pair(String username, String date) {
                this.username = username;
                this.date = date;
            }

        }

    }

    /*https://developer.android.com/training/basics/fragments/communicating*/
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RequestsFragment) {
            RequestsFragment.setOnConnectionAddedListener(this);
        } else if(fragment instanceof ConnectionsFragment) {
            ConnectionsFragment.setOnConnectionRemovedListener(this);
        }
    }

    /*UPDATE DATA INTENT THROUGH INTERFACE*/
    public void onConnectionAdded(String connection) {

        Log.i("com.x10host", connection);
        /*Add the new connection to the array and update the fragments with the new data*/
        if(FragmentUtils.isEmpty(this.connectionResults)) {
            this.connectionResults = (String[]) ArrayUtils.appendToArray(this.connectionResults, connection);
        }
        else {
            this.connectionResults = (String[]) ArrayUtils.appendToArray(this.connectionResults, connection);
        }

        this.receivedResults = (String[])ArrayUtils.removeAll(this.receivedResults, connection);

        updateResultSet(this.arrResults, this.connectionResults, this.requestResults, this.receivedResults);
        populateComponents(adapter.getResults(), this.connectionResults, this.requestResults, this.receivedResults);

    }

    public void onRequestCancelled(String request) {

        this.receivedResults = (String[])ArrayUtils.removeAll(this.receivedResults, request);
        this.requestResults = (String[])ArrayUtils.removeAll(this.requestResults, request);

        updateResultSet(this.arrResults, this.connectionResults, this.requestResults, this.receivedResults);
        populateComponents(adapter.getResults(), this.connectionResults, this.requestResults, this.receivedResults);

    }

    public void onConnectionRemoved(String connection) {
        this.connectionResults = (String[]) ArrayUtils.removeAll(this.connectionResults, connection);
        updateResultSet(this.arrResults, this.connectionResults, this.requestResults, this.receivedResults);
        populateComponents(adapter.getResults(), this.connectionResults, this.requestResults, this.receivedResults);
    }

    private void parseUpdate(String json) throws JSONException, InterruptedException {

        Log.i("com.x10host", json);

        JSONObject jsonObject = new JSONObject(json);
        boolean itemsChanged = (jsonObject.has("payload") || jsonObject.has("names_0")
                                    || jsonObject.has("names_1") || jsonObject.has("names_2")
                                    || jsonObject.has("DELETE_CONNECTION") || jsonObject.has("REMOVE_REQUEST"));

        /*Handle the main entries received that are not on the device*/
        List<String> list = new LinkedList<String>(Arrays.asList(this.arrResults));
        if(jsonObject.has("payload")) {
            String[] payload = jsonObject.getString("payload").split("\n");
            for (int i = payload.length - 1; i >= 0; i--) {
                list.add(0, payload[i]);
            }
            this.arrResults = list.toArray(new String[list.size()]);
        }

        /*Handle the connections received that are not on the device*/
        list = new LinkedList<String>(Arrays.asList(this.connectionResults));
        if(jsonObject.has("names_0")) {
            for(String connection : jsonObject.getString("names_0").split("\n")) {

                if(!list.contains(connection)) {
                    list.add(0, connection);
                }

                /*If the connection has been accepted, the requests no longer should exist*/
                this.requestResults = (String[]) ArrayUtils.removeAll(this.requestResults, connection);
                this.receivedResults = (String[]) ArrayUtils.removeAll(this.receivedResults, connection);
            }
            this.connectionResults = list.toArray(new String[list.size()]);
        }

        /*Handle the requests sent that are not on the device*/
        list = new LinkedList<String>(Arrays.asList(this.requestResults));
        if(jsonObject.has("names_1")) {
            for(String requestResult : jsonObject.getString("names_1").split("\n")) {
                list.add(0, requestResult);
            }
            this.requestResults = list.toArray(new String[list.size()]);
        }

        /*Handle the requests received that are not on the device*/
        list = new LinkedList<String>(Arrays.asList(this.receivedResults));
        if(jsonObject.has("names_2")) {
            for(String receivedResult : jsonObject.getString("names_2").split("\n")) {
                list.add(0, receivedResult);
            }
            this.receivedResults = list.toArray(new String[list.size()]);
        }

        if(jsonObject.has("DELETE_CONNECTION")) {
            for(String connection : jsonObject.getString("DELETE_CONNECTION").split("\n")) {
                if(!connection.isEmpty()) {
                    this.connectionResults = (String[]) ArrayUtils.removeAll(this.connectionResults, connection);
                }
            }
        }

        if(jsonObject.has("REMOVE_REQUEST")) {
            for(String request : jsonObject.getString("REMOVE_REQUEST").split("\n")) {
                if(!request.isEmpty()) {
                    this.requestResults = (String[]) ArrayUtils.removeAll(this.requestResults, request);
                    this.receivedResults = (String[]) ArrayUtils.removeAll(this.receivedResults, request);
                }
            }
        }

        last_ids[0] = (!jsonObject.has("last_id_0") ? last_ids[0] :
                Integer.parseInt(jsonObject.getString("last_id_0")));

        last_ids[1] = (!jsonObject.has("last_id_1") ? last_ids[1] :
                Integer.parseInt(jsonObject.getString("last_id_1")));

        last_ids[2] = (!jsonObject.has("last_id_2") ? last_ids[2] :
                Integer.parseInt(jsonObject.getString("last_id_2")));

        /*Don't update components unless an item was changed*/

        if(itemsChanged) {

            /*DO NOT Allow for there to be more than the allocated elements*/
            if(this.arrResults.length > MAX_SIZE) {
                this.arrResults = Arrays.copyOf(this.arrResults, MAX_SIZE);
            }

            populateComponents(utilsSaved != null ? utilsSaved.getArrResult(this.arrResults) : this.arrResults, this.connectionResults, this.requestResults, this.receivedResults);
        }

        if(jsonObject.has("last_id_3") && !jsonObject.getString("last_id_3").equals("null")) {

            final FormPost<String, String> actionPost = new FormPost<>();

            actionPost.addPair("username", getIntent().getExtras().getString("username"));
            actionPost.addPair("password", getIntent().getExtras().getString("password"));
            actionPost.addPair("last_id", jsonObject.getString("last_id_3"));

            Thread thread = new Thread(new Runnable() {
               @Override
               public void run() {
                   /*DELETE INTERMEDIARY RESULTS*/
                   actionPost.submitPost(deleteActionResults, MethodType.POST);
               }
            });

            thread.start();
            thread.join();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }

    @Override protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnableCode);
    }

    @Override protected void onResume() {
        super.onResume();
        handler.removeCallbacksAndMessages(null);
        handler.post(runnableCode);
    }

}
