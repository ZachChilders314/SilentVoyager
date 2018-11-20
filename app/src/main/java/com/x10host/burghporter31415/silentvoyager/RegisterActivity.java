package com.x10host.burghporter31415.silentvoyager;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class RegisterActivity extends AppCompatActivity {

    private String PATH;
    private VideoView introVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle data = getIntent().getExtras();
        PATH = data.getString("PATH");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        introVideoView = (VideoView) findViewById(R.id.introVideoView);

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

    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    }

}
