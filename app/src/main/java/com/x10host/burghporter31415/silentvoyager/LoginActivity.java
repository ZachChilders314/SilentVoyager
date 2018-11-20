package com.x10host.burghporter31415.silentvoyager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

public class LoginActivity extends AppCompatActivity {

    private String PATH;
    private VideoView introVideoView;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle data = getIntent().getExtras();
        PATH = data.getString("PATH");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin       = (Button) findViewById(R.id.btnLogin);
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

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(getApplicationContext(), Dashboard.class);
                loginIntent.putExtra("PATH", PATH);
                startActivity(loginIntent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    }

}
