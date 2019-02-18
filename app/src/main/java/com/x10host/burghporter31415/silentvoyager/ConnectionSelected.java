package com.x10host.burghporter31415.silentvoyager;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.fragments.FragmentUtils;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

public class ConnectionSelected extends AppCompatActivity {

    private VideoView introVideoView;
    private EditText txtConnectionName;
    private EditText txtConnectionUsername;

    private Button btnRemoveConnection;
    private Button btnConnectionReturn;

    private final String BASE_URL = "http://burghporter31415.x10host.com";
    private final String RELATIVE_URL = "/Silent_Voyager/App_Scripts/remove_connection.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_selected);

        Bundle bundle = getIntent().getExtras();

        final String connectionName = bundle.getString("connectionName");
        final String connectionUsername = bundle.getString("connectionUsername");

        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        Toast.makeText(this, connectionName + " (@" + connectionUsername + ")", Toast.LENGTH_LONG).show();

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

        txtConnectionName = (EditText) findViewById(R.id.txtConnectionName);
        txtConnectionUsername = (EditText) findViewById(R.id.txtConnectionUsername);

        btnRemoveConnection = (Button) findViewById(R.id.btnRemoveConnection);
        btnConnectionReturn = (Button) findViewById(R.id.btnConnectionReturn);

        txtConnectionName.setText(connectionName);
        txtConnectionUsername.setText(connectionUsername);

        btnRemoveConnection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {

                            FormPost<String, String> connection = new FormPost<String, String>();

                            PHPPage page = new PHPPage(BASE_URL, RELATIVE_URL);

                            /*Sent in the header of POST request*/
                            connection.addPair("connectionName", connectionName);
                            connection.addPair("connectionUsername", connectionUsername);

                            connection.addPair("username", username);
                            connection.addPair("password", password);

                            String result = connection.submitPost(page, MethodType.POST);

                        } catch(Exception e) {
                            Log.i("com.x10host", "Credential Validation Failed");
                        }

                    }
                });

                try {
                    thread.start();
                    thread.join();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //TODO
                }

                Toast.makeText(ConnectionSelected.this, "Function Not Supported Yet", Toast.LENGTH_LONG).show();

            }
        });

        btnConnectionReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }
}
