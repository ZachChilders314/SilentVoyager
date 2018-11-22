package com.x10host.burghporter31415.silentvoyager;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.internetservices.InternetPHPRequestService;

public class LoginActivity extends AppCompatActivity {

    private String PATH;
    private VideoView introVideoView;
    private Button btnLogin;

    private EditText txtUsername;
    private EditText txtPassword;

    private final String BASE_URL = "http://burghporter31415.x10host.com";
    private final String RELATIVE_URL = "/Silent_Voyager/App_Scripts/validate_credentials.php";

    private final String FAILED_LOGIN = "ERROR: Invalid Credentials";
    private final String SUCCESSFUL_LOGIN = "Success: Loading Dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle data = getIntent().getExtras();
        PATH = data.getString("PATH");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin       = (Button) findViewById(R.id.btnLogin);
        introVideoView = (VideoView) findViewById(R.id.introVideoView);
        txtUsername    = (EditText) findViewById(R.id.txtUsername);
        txtPassword    = (EditText) findViewById(R.id.txtPassword);


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

                Intent intent = new Intent(getApplicationContext(), InternetPHPRequestService.class);

                intent.putExtra("username", txtUsername.getText().toString());
                intent.putExtra("password", txtPassword.getText().toString());

                intent.putExtra("BASE_URL", BASE_URL);
                intent.putExtra("RELATIVE_URL", RELATIVE_URL);

                PendingIntent pendingResult = createPendingResult(100, new Intent(), 0);
                intent.putExtra("pendingIntent", pendingResult);

                startService(intent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode==200) {

            String result = data.getStringExtra("result");

            if(result.equals("VALID")) {
                Toast.makeText(this,SUCCESSFUL_LOGIN,Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), Dashboard.class);

                intent.putExtra("username", data.getStringExtra("username"));
                intent.putExtra("password", data.getStringExtra("password"));

                startActivity(intent);

            } else {
                Toast.makeText(this,FAILED_LOGIN,Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    }

}
