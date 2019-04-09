package com.x10host.burghporter31415.silentvoyager;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.internetservices.InternetPHPRequestService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private String PATH;
    private VideoView introVideoView;
    private Button btnRegister;

    private EditText txtName;
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;

    private final String BASE_URL = "http://burghporter31415.x10host.com";
    private final String RELATIVE_URL = "/Silent_Voyager/App_Scripts/add_user.php";

    private final String FAILED_LOGIN = "ERROR: ";
    private final String FAILED_LOGIN_EMAIL = "ERROR: Invalid Email";
    private final String FAILED_LOGIN_PASSWORD_MATCH = "ERROR: Passwords do not match!";
    private final String SUCCESSFUL_LOGIN = "Success: Loading Dashboard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));

        Bundle data = getIntent().getExtras();
        PATH = data.getString("PATH");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister        = (Button)   findViewById(R.id.btnRegister);
        txtName            = (EditText) findViewById(R.id.txtName);
        txtUsername        = (EditText) findViewById(R.id.txtUsername);
        txtEmail           = (EditText) findViewById(R.id.txtEmail);
        txtPassword        = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

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

        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(!isValidEmail(txtEmail.getText().toString())) {

                    Toast.makeText(getApplicationContext(), FAILED_LOGIN_EMAIL,Toast.LENGTH_LONG).show();

                } else if(!txtPassword.getText().toString()
                                    .equals(txtConfirmPassword.getText().toString())){

                    Toast.makeText(getApplicationContext(), FAILED_LOGIN_PASSWORD_MATCH,Toast.LENGTH_LONG).show();

                } else {

                    Intent intent = new Intent(getApplicationContext(), InternetPHPRequestService.class);

                    intent.putExtra("name", txtName.getText().toString());
                    intent.putExtra("username", txtUsername.getText().toString());
                    intent.putExtra("email", txtEmail.getText().toString());
                    intent.putExtra("password", txtPassword.getText().toString());
                    intent.putExtra("confirmpassword", txtConfirmPassword.getText().toString());

                    intent.putExtra("BASE_URL", BASE_URL);
                    intent.putExtra("RELATIVE_URL", RELATIVE_URL);

                    PendingIntent pendingResult = createPendingResult(100, new Intent(), 0);
                    intent.putExtra("pendingIntent", pendingResult);

                    startService(intent);

                }
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
                Toast.makeText(this,FAILED_LOGIN + result,Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /*
     * Taken From: https://stackoverflow.com/questions/9355899/android-email-edittext-validation
     * Author: Mahendra Liya
     *
     */

    public boolean isValidEmail(CharSequence target) {

        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

    }

    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    }

}
