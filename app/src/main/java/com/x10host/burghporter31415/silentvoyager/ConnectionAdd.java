package com.x10host.burghporter31415.silentvoyager;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class ConnectionAdd extends AppCompatActivity {

    private VideoView introVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conection);

        introVideoView = (VideoView) findViewById(R.id.introVideoView);

        introVideoView.setVideoPath(getIntent().getExtras().getString("PATH"));
        introVideoView.requestFocus();
        introVideoView.start();

        introVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    }

}
