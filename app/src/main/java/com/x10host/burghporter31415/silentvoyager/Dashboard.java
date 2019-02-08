package com.x10host.burghporter31415.silentvoyager;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.x10host.burghporter31415.fragments.DashboardPagerAdapter;
import com.x10host.burghporter31415.internetservices.BackgroundServiceBroadcast;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

public class Dashboard extends AppCompatActivity {

    private ViewPager viewPager;
    private DashboardPagerAdapter adapter;
    private Button btnFilterOptions;

    private final String FILE_NAME="Silent_Voyager_Credentials.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnFilterOptions = (Button) findViewById(R.id.btnFilterOptions);

        btnFilterOptions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), Filter.class);

                /*Need the username for the Filter activity to query the database for common users*/
                intent.putExtra("username", getIntent().getExtras().getString("username"));
                intent.putExtra("password", getIntent().getExtras().getString("password"));

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
        final PHPPage resultRequestPage = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/get_results.php");

        final FormPost<String, String> resultFormPost = new FormPost<>();
        resultFormPost.addPair("username", getIntent().getExtras().getString("username"));
        resultFormPost.addPair("password", getIntent().getExtras().getString("password"));

        try {

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    String result = resultFormPost.submitPost(resultRequestPage, MethodType.POST);
                    String[] arr = result.split("\n"); //Row contains: Username, lat, long, alt, City, Datestamp
                    populateComponents(arr);
                }
            });

            thread.start();
            thread.join(); /*Wait for the thread to finish, and then create executor service to split tasks*/

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*Callback function after results have been returned for current user*/
    private void populateComponents(final String[] arr) {

        adapter.setData(arr);
        adapter.notifyDataSetChanged();

        //try {

            /*Create a new thread for each core of the phone--this may not be optimal for Dual-Core
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            final Context context = this;

            executor.submit(new Runnable() {
                @Override
                public void run() {


                }
            });

            executor.submit(new Runnable() {
                @Override
                public void run() {

                }
            });

            executor.submit(new Runnable() {
                @Override
                public void run() {

                }
            });

            executor.shutdown(); //No longer accept tasks for executor
            executor.awaitTermination(10, TimeUnit.MINUTES); //Timeout after 10 minutes

        } catch(InterruptedException e) {
            e.printStackTrace();
        } */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        /*The request and result codes need to be set accordingly in the activities*/
        if (requestCode == 100 && resultCode==200) {

            /*Parse the Data Passed (usernameSelection, maxEntriesSelection, startDate, endDate)*/
            Bundle savedBundle = data.getExtras();

            String username = savedBundle.getString("usernameSelection");
            String maxEntries = savedBundle.getString("maxEntriesSelection");

            String startDate = savedBundle.getString("startDate");
            String endDate = savedBundle.getString("endDate");

            Log.i("com.x10host", username + ", " + maxEntries + ", " + startDate + ", " + endDate);

        }

        super.onActivityResult(requestCode, resultCode, data);

    }


}
