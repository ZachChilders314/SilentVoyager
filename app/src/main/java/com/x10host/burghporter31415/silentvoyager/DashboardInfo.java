package com.x10host.burghporter31415.silentvoyager;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.fragments.FragmentUtils;
import com.x10host.burghporter31415.internetservices.InternetPHPRequestService;

public class DashboardInfo extends AppCompatActivity {

    private VideoView introVideoView;
    private TextView lblUsernameDisplay;
    private TextView lblLatitudeDisplay;
    private TextView lblLongitudeDisplay;
    private TextView lblAltitudeDisplay;
    private TextView lblCityDisplay;
    private TextView lblDateDisplay;
    private Button btnReturn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_info);
        Bundle bundle = getIntent().getExtras();

        String username = bundle.getString("username");
        String latitude = bundle.getString("latitude");
        String longitude = bundle.getString("longitude");
        String altitude = bundle.getString("altitude");
        String city = bundle.getString("city");
        String datestamp = FragmentUtils.returnDateStamp(bundle.getString("datestamp").split("_"), true);

        Toast.makeText(this, datestamp,Toast.LENGTH_LONG).show();

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


        lblUsernameDisplay = (TextView) findViewById(R.id.lblUsernameDisplay);
        lblLatitudeDisplay = (TextView) findViewById(R.id.lblLatitudeDisplay);
        lblLongitudeDisplay = (TextView) findViewById(R.id.lblLongitudeDisplay);
        lblAltitudeDisplay = (TextView) findViewById(R.id.lblAltitudeDisplay);
        lblCityDisplay = (TextView) findViewById(R.id.lblCityDisplay);
        lblDateDisplay = (TextView) findViewById(R.id.lblDateDisplay);
        btnReturn = (Button) findViewById(R.id.btnReturn);

        lblUsernameDisplay.setText(username);
        lblLatitudeDisplay.setText(latitude);
        lblLongitudeDisplay.setText(longitude);
        lblAltitudeDisplay.setText(altitude);
        lblCityDisplay.setText(city);
        lblDateDisplay.setText(datestamp);

        btnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    } */
}
