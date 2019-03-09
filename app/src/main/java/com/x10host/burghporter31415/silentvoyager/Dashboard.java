package com.x10host.burghporter31415.silentvoyager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.fragments.DashboardPagerAdapter;
import com.x10host.burghporter31415.internetservices.BackgroundServiceBroadcast;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private VideoView introVideoView;

    private ViewPager viewPager;
    private DashboardPagerAdapter adapter;
    private Button btnFilterOptions;
    private Button btnAddConnection;

    private final String FILE_NAME="Silent_Voyager_Credentials.txt";
    private String[] arrResults = null;
    private String[] connectionResults = null;
    private String[] requestResults = null;

    final FormPost<String, String> resultFormPost = new FormPost<>();
    final PHPPage resultRequestPage = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/get_results.php");
    final PHPPage resultRequestPageResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/connection_results.php");
    final PHPPage resultRequestPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/request_connection_results.php");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        /*
        introVideoView = (VideoView) findViewById(R.id.introVideoView);

        introVideoView.setVideoPath(getIntent().getExtras().getString("PATH"));
        introVideoView.requestFocus();
        introVideoView.start();

        introVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        }); */

        btnFilterOptions = (Button) findViewById(R.id.btnFilterOptions);

        btnFilterOptions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Filter.class);

                /*Need the username for the Filter activity to query the database for common users*/
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                intent.putExtra("password", getIntent().getExtras().getString("password"));

                intent.putExtra("PATH", getIntent().getExtras().getString("PATH"));

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

                startActivityForResult(intent, 100);

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
                    String[] connectionResults = resultFormPost.submitPost(resultRequestPageResults, MethodType.POST).split("\n");
                    String[] requestResults = resultFormPost.submitPost(resultRequestPageRequestResults, MethodType.POST).split("\n");

                    updateResultSet(results, connectionResults,requestResults);
                    populateComponents(results, connectionResults, requestResults);

                }
            });

            thread.start();
            thread.join(); /*Wait for the thread to finish, and then create executor service to split tasks*/

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*Callback function after results have been returned for current user*/
    private void populateComponents(String[] results, String[] connectionResults, String[] requestResults) {

        adapter.setData(results, connectionResults, requestResults);
        adapter.notifyDataSetChanged();

    }

    private void updateResultSet(String[] arrResults, String[] connectionResults, String[] requestResults) {
        /*{Location data, connection data, and request data}*/
        this.arrResults = arrResults;
        this.connectionResults = connectionResults;
        this.requestResults = requestResults;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*The request and result codes need to be set accordingly in the activities*/
        if (requestCode == 100 && resultCode==200) {

            /*Parse the Data Passed (usernameSelection, maxEntriesSelection, startDate, endDate)*/
            Bundle savedBundle = data.getExtras();

            final String username = savedBundle.getString("usernameSelection");
            final String maxEntries = savedBundle.getString("maxEntriesSelection");

            final String startDate = savedBundle.getString("startDate");
            final String endDate = savedBundle.getString("endDate");

            /******************************************DONE IN THREAD AND THREAD MAIN**********************************************/

            final String[] arrResults = this.arrResults;

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    final FilterUtils utils = new FilterUtils(username, Integer.parseInt(maxEntries), startDate, endDate);

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            populateComponents(utils.getArrResult(arrResults), connectionResults, requestResults);
                        }
                    });

                }
            });

            try {
                thread.start();
                thread.join(); /*Wait for the thread to finish, and then create executor service to split tasks*/
            } catch (InterruptedException e) {}

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

    /*
    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    } */

}
