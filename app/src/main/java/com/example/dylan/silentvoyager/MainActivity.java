package com.example.dylan.silentvoyager;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView introVideoView;
    private Button btnLogin;

    private String PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PATH = "android.resource://"+getPackageName()+"/"+R.raw.fly_by;
        introVideoView = (VideoView) findViewById(R.id.introVideoView);
        btnLogin       = (Button) findViewById(R.id.btnLogin);

        introVideoView.setVideoPath(this.PATH);

        introVideoView.requestFocus();
        introVideoView.start();

        introVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

    }

}
