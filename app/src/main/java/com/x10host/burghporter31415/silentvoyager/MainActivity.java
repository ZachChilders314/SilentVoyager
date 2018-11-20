package com.x10host.burghporter31415.silentvoyager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    
    private final String[] ACTIVITY_PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECORD_AUDIO
    };

    private final int REQUEST_INTERNET = 0;
    private final int REQUEST_LOCATION = 1;
    private final int REQUEST_AUDIO    = 2;
    private final int REQUEST_MULTIPLE = 3;

    private VideoView introVideoView;
    private Button btnLogin;
    private Button btnRegister;

    private final String ERROR_MESSAGE_PERMISSIONS = "NOTICE: All Permissions Required to Proceed!";
    private final String SUCCESS_MESSAGE_PERMISSIONS = "NOTICE: All Permissions Granted.";
    private String PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PATH = "android.resource://" + getPackageName() + "/" + R.raw.background_video;

        introVideoView = (VideoView) findViewById(R.id.introVideoView);
        btnLogin       = (Button) findViewById(R.id.btnLogin);
        btnRegister    = (Button) findViewById(R.id.btnRegister);

        introVideoView.setVideoPath(this.PATH);
        introVideoView.requestFocus();
        introVideoView.start();

        introVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(checkIfPermissionsGranted(ACTIVITY_PERMISSIONS)) {

                    Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                    registerIntent.putExtra("PATH", PATH);
                    startActivity(registerIntent);

                } else {

                    Toast.makeText(getApplicationContext(), ERROR_MESSAGE_PERMISSIONS,
                            Toast.LENGTH_LONG).show();

                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(checkIfPermissionsGranted(ACTIVITY_PERMISSIONS)) {

                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    loginIntent.putExtra("PATH", PATH);
                    startActivity(loginIntent);

                } else {

                    Toast.makeText(getApplicationContext(), ERROR_MESSAGE_PERMISSIONS,
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    @Override
    protected void onResume() {

        super.onResume();
        introVideoView.start();

    }

    private String[] getNonGrantedPermissions(String[] permissions) {

        List<String> nonGrantedPermissions = new ArrayList<String>();

        for(String item : permissions) {

            if(ContextCompat.checkSelfPermission(this,
                    item) != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(item);
            }
        }

        return nonGrantedPermissions.toArray(new String[nonGrantedPermissions.size()]);
    }

    private void requestPermissions(String[] permissions) {

        ActivityCompat.requestPermissions(this,
                permissions, REQUEST_MULTIPLE);

    }

    private boolean checkIfPermissionsGranted(String[] permissionsList) {

        if(getNonGrantedPermissions(permissionsList).length != 0) {

            String[] permissionsDenied
                    = getNonGrantedPermissions(permissionsList);

            synchronized (this) {
                requestPermissions(permissionsDenied);
            }

            return false;

        } else {  return true;  }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch(requestCode) {
            case REQUEST_INTERNET: {
                //TODO
                break;
            }
            case REQUEST_LOCATION: {
                //TODO
                break;
            }
            case REQUEST_AUDIO: {
                //TODO
                break;
            }
            case REQUEST_MULTIPLE: {
                //TODO
                break;
            }
        }
    }
}
