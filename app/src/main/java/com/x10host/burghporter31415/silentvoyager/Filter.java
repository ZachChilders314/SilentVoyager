package com.x10host.burghporter31415.silentvoyager;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.x10host.burghporter31415.TimePicker.DialogDateFragment;
import com.x10host.burghporter31415.TimePicker.DialogTimeFragment;

//https://stackoverflow.com/questions/14292398/how-to-pass-data-from-2nd-activity-to-1st-activity-when-pressed-back-android NEED THIS

public class Filter extends AppCompatActivity {

    final DialogFragment dateFragmentStart = new DialogDateFragment();
    final DialogFragment timeFragmentStart = new DialogTimeFragment();

    final DialogFragment dateFragmentEnd = new DialogDateFragment();
    final DialogFragment timeFragmentEnd = new DialogTimeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        /*Set the adapters first -- I will need to make a PHP request to get all usernames associated with current*/
        String[] arr = {getIntent().getExtras().getString("username")}; //Temporary Provision
        String[] arr2 = {"10", "25", "100", "1000"};

        /*All Preliminary Stuff to set up Spinners*/
        Spinner spinnerName = (Spinner) findViewById(R.id.spinnerName);
        Spinner spinnerMaxEntries = (Spinner) findViewById(R.id.spinnerMaxEntries);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        spinnerMaxEntries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        /*Preliminary stuff for the Datepickers*/
        DatePicker pickerStartDate = (DatePicker) findViewById(R.id.pickerStartDate);
        DatePicker pickerEndDate = (DatePicker) findViewById(R.id.pickerEndDate);

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

                    //timeFragment.show(getSupportFragmentManager(), "timePicker");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*Handle the pending intent based off of the code that was unique to Start Date and End Date Widget*/
        switch(resultCode) {
            case 100: timeFragmentStart.show(getSupportFragmentManager(), "timePicker");
                break;
            case 200: Log.i("com.x10host", resultCode + "");
                break;
            case 300: timeFragmentEnd.show(getSupportFragmentManager(), "timePicker");
                break;
            case 400: Log.i("com.x10host", resultCode + "");
                break;
            default:
                break;
        }
    }

}
