package com.x10host.burghporter31415.silentvoyager;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.common.util.ArrayUtils;
import com.x10host.burghporter31415.TimePicker.DateUtil;
import com.x10host.burghporter31415.TimePicker.DialogDateFragment;
import com.x10host.burghporter31415.TimePicker.DialogTimeFragment;
import com.x10host.burghporter31415.fragments.FragmentUtils;

//https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android NEED THIS

public class Filter extends AppCompatActivity {

    private VideoView introVideoView;

    final private DialogFragment dateFragmentStart = new DialogDateFragment();
    final private DialogFragment timeFragmentStart = new DialogTimeFragment();

    final private DialogFragment dateFragmentEnd = new DialogDateFragment();
    final private DialogFragment timeFragmentEnd = new DialogTimeFragment();

    private Button pickerStartDate;
    private Button pickerEndDate;

    /*This will contain Username, Max Entries, Start Date, End Date*/
    private Intent dataIntent = new Intent();

    DateUtil startDate = new DateUtil();
    DateUtil endDate = new DateUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        /*introVideoView = (VideoView) findViewById(R.id.introVideoView);

        introVideoView.setVideoPath(getIntent().getExtras().getString("PATH"));
        introVideoView.requestFocus();
        introVideoView.start();

        introVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        }); */

        /*Populate Spinner with username and associative connections*/

        String[] connections = (String[])getIntent().getExtras().get("connections");
        String[] arr = new String[FragmentUtils.isEmpty(connections) ? 1 : connections.length + 1];

        arr[0] = (String) getIntent().getExtras().get("username");

        if(!FragmentUtils.isEmpty(connections)) {
            for (int i = 0; i < connections.length; i++) {
                arr[i + 1] = FragmentUtils.returnParsedUsernameCluster(connections[i]);
            }
        }

        String[] arr2 = {"10", "25", "100", "500"};

        /*All Preliminary Stuff to set up Spinners*/
        final Spinner spinnerName = (Spinner) findViewById(R.id.spinnerName);
        final Spinner spinnerMaxEntries = (Spinner) findViewById(R.id.spinnerMaxEntries);

        ArrayAdapter<String> adapterName = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, arr);

        ArrayAdapter<String> adapterMaxEntries = new ArrayAdapter<String>(this,
                                android.R.layout.simple_spinner_dropdown_item, arr2);

        adapterName.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapterMaxEntries.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinnerName.setAdapter(adapterName);
        spinnerMaxEntries.setAdapter(adapterMaxEntries);

        /*Set the text color of the Spinner programmatically (Hard to do through XML)*/
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        spinnerMaxEntries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        /*Preliminary stuff for the Datepickers*/
        this.pickerStartDate = (Button) findViewById(R.id.pickerStartDate);
        this.pickerEndDate = (Button) findViewById(R.id.pickerEndDate);

        /*Date and Time Picker fragments used when the user 'clicks' the startdate and enddate datepicker widgets*/
        PendingIntent pendingIntent = createPendingResult(100, new Intent(), 0);

        ((DialogDateFragment) dateFragmentStart).setPendingIntent(pendingIntent, 100);
        ((DialogTimeFragment) timeFragmentStart).setPendingIntent(pendingIntent, 200);

        ((DialogDateFragment) dateFragmentEnd).setPendingIntent(pendingIntent, 300);
        ((DialogTimeFragment) timeFragmentEnd).setPendingIntent(pendingIntent, 400);

        pickerStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    /*Start the first dialog in a concurrent environment and wait for its termination before displaying the time picker*/
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                synchronized(this) {
                                    dateFragmentStart.show(getSupportFragmentManager(), "datePicker");
                                }
                            }
                        });

                        thread.start();
                        thread.join();

                        //timeFragment.show(getSupportFragmentManager(), "timePicker");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

        pickerEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    /*Start the first dialog in a concurrent environment and wait for its termination before displaying the time picker*/
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            synchronized(this) {
                                dateFragmentEnd.show(getSupportFragmentManager(), "datePicker");
                            }
                        }
                    });

                    thread.start();
                    thread.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

        /*Preliminary Stuff for Save Button*/
        Button btnFilterSave = (Button) findViewById(R.id.btnFilterSave);

        btnFilterSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Pass Bundle to Activity for Result (I.E. Dashboard Script)
                dataIntent.putExtra("usernameSelection", spinnerName.getSelectedItem().toString());
                dataIntent.putExtra("maxEntriesSelection", spinnerMaxEntries.getSelectedItem().toString());
                setResult(200, dataIntent);
                finish();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Handle the pending intent based off of the code that was unique to Start Date and End Date Widget*/
        Bundle bundle = data.getExtras(); //Would have d1, d2, d3 parameters

        switch(resultCode) {

            case 100:

                this.startDate.setDate(bundle.getInt("d1"), bundle.getInt("d2") + 1, bundle.getInt("d3"));
                timeFragmentStart.show(getSupportFragmentManager(), "timePicker");
                break;

            case 200:

                this.startDate.setTime(bundle.getInt("d1"), bundle.getInt("d2"));
                this.dataIntent.putExtra("startDate", this.startDate.toString());
                this.pickerStartDate.setText(FragmentUtils.returnDateStamp(this.startDate.toString().split("_"), false));
                break;

            case 300:

                this.endDate.setDate(bundle.getInt("d1"), bundle.getInt("d2") + 1, bundle.getInt("d3"));
                timeFragmentEnd.show(getSupportFragmentManager(), "timePicker");
                break;

            case 400:

                this.endDate.setTime(bundle.getInt("d1"), bundle.getInt("d2"));
                this.dataIntent.putExtra("endDate", this.endDate.toString());
                this.pickerEndDate.setText(FragmentUtils.returnDateStamp(this.endDate.toString().split("_"), false));
                break;

            default:
                break;
        }
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    } */

}
