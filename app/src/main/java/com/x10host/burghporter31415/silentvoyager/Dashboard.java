package com.x10host.burghporter31415.silentvoyager;

import android.content.Intent;
import android.media.MediaPlayer;
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
import com.x10host.burghporter31415.fragments.DashboardPagerAdapter;
import com.x10host.burghporter31415.fragments.RequestsFragment;
import com.x10host.burghporter31415.internetservices.BackgroundServiceBroadcast;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity implements RequestsFragment.OnConnectionAddedListener {

    private VideoView introVideoView;

    private ViewPager viewPager;
    private DashboardPagerAdapter adapter;
    private Button btnFilterOptions;
    private Button btnAddConnection;

    private final String FILE_NAME="Silent_Voyager_Credentials.txt";
    private String currentUsername;

    private HashMap<String, String[]> userHash = new HashMap<String, String[]>();
    private HashMap<String, Integer[]> last_ids = new HashMap<String, Integer[]>();

    private String[] arrResults = null;
    private String[] connectionResults = null;
    private String[] requestResults = null;
    private String[] receivedResults = null;

    final FormPost<String, String> resultFormPost = new FormPost<>();
    final PHPPage resultRequestPage = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/get_results.php");
    final PHPPage resultRequestPageResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/connection_results.php");
    final PHPPage resultRequestPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/request_connection_results.php");
    final PHPPage resultReceivedPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/received_connection_results.php");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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

                    //TODO Put on timer
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
                        Integer[] keys = {connectionResultsJSON.getLastId(), requestResultsJSON.getLastId(), receivedResultsJSON.getLastId()};
                        last_ids.put(getIntent().getExtras().getString("username"), keys);

                    } catch (JSONException e) {}

                    populateComponents(results, connectionResults, requestResults, receivedResults);

                }
            });

            thread.start();
            thread.join();

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
                    updateResultSet(userHash.get(username), connectionResults, requestResults, receivedResults);
                    populateComponents(userHash.get(username), connectionResults, requestResults, receivedResults);
                }
                currentUsername = username; /*A different user has been selected, collect the data and insert into hash*/

            } else {

                final String[] arrResults = this.arrResults;

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        final FilterUtils utils = new FilterUtils(username, Integer.parseInt(maxEntries), startDate, endDate);

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

        } else if(requestCode == 200 && resultCode == 200) {

            savedBundle = data.getExtras();

            final String userRequested = savedBundle.getString("userRequested");

            if(this.requestResults[0].isEmpty()) {
                this.requestResults[0] = userRequested;
            } else {
                this.requestResults = (String[]) ArrayUtils.appendToArray(this.requestResults, userRequested);
            }

            populateComponents(adapter.getResults(), this.connectionResults, this.requestResults, this.receivedResults);

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

            if(this.beginDate == null || this.endDate == null) {return false;}

            return (this.username.equals(pair.username)
                   && (this.beginDate.compareTo(pair.date) < 0)
                   && (this.endDate.compareTo(pair.date) > 0));

        }

        public String[] getArrResult(String[] arr) {
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
            RequestsFragment headlinesFragment = (RequestsFragment) fragment;
            RequestsFragment.setOnConnectionAddedListener(this);
        }
    }

    /*UPDATE DATA INTENT THROUGH INTERFACE*/
    public void onConnectionAdded(String connection) {

        /*Add the new connection to the array and update the fragments with the new data*/
        if(this.connectionResults[0].isEmpty())
            this.connectionResults[0] = connection;
        else
            this.connectionResults = (String[]) ArrayUtils.appendToArray(this.connectionResults, connection);

        this.receivedResults = (String[])ArrayUtils.removeAll(this.receivedResults, connection);
        populateComponents(adapter.getResults(), this.connectionResults, this.requestResults, this.receivedResults);

    }



    /*
    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    } */

}
