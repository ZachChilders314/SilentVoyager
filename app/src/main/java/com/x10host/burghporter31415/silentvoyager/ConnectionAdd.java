package com.x10host.burghporter31415.silentvoyager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.x10host.burghporter31415.fragments.FragmentUtils;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConnectionAdd extends AppCompatActivity {

    private VideoView introVideoView;
    private Button btnSearch;
    private Button btnAddConnectionReturn;
    private EditText txtSearch;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    private String[] removableUsernames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conection);

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

        /*Make a connection to the server to display results on click of the search button*/


        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnAddConnectionReturn = (Button) findViewById(R.id.btnAddConnectionReturn);

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        listView = (ListView)findViewById(R.id.list_view_results);

        final FormPost<String, String> resultFormPost = new FormPost<>();
        final PHPPage resultRequestPageSearch = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/user_match.php");
        final PHPPage resultRequestPageRequest = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/request_connection.php");
        final PHPPage resultRequestPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/request_connection_results.php");
        final PHPPage resultReceivedRequestPageRequestResults = new PHPPage("http://burghporter31415.x10host.com/Silent_Voyager", "/App_Scripts/Connection_Scripts/received_connection_results.php");

        resultFormPost.addPair("username", getIntent().getExtras().getString("username"));
        resultFormPost.addPair("password", getIntent().getExtras().getString("password"));

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {

                    /*Close the keyboard on button press*/
                    /*Credit to: https://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press*/

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                }
                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultFormPost.addPair("search_param", txtSearch.getText().toString());
                final ArrayList<String> listItems = new ArrayList<String>();
                final Boolean[] resultsFound = {false}; //Since it needs to be final for the async task, I need the value to be a memory location so I can change it in the runnable

                try {

                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {

                            String[] result = new String[0];
                            result = resultFormPost.submitPost(resultRequestPageSearch, MethodType.POST).split("\n");
                            String username = getIntent().getExtras().getString("username");

                            if(result[0].isEmpty()) {

                                listItems.add("No Results Found");

                            } else {

                                for (int i = 0; i < result.length; i++) {
                                    if(!result[i].toLowerCase().equals(username.toLowerCase())) {
                                        listItems.add(result[i]);
                                    }
                                }

                                resultsFound[0] = true;

                            }

                            /*Close the keyboard on button press*/
                            /*Credit to: https://stackoverflow.com/questions/3400028/close-virtual-keyboard-on-button-press*/

                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                        }
                    });

                    thread.start();
                    thread.join();

                    adapter = new ArrayAdapter<String>(ConnectionAdd.this, R.layout.text_view_list, listItems);

                    listView.setAdapter(adapter);

                    resultFormPost.removePair("search_param"); /*Don't need in header body*/

                    ArrayList<String> removedUsernames = new ArrayList<String>();

                    Thread threadRequestedUsernames = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /*Remove Results that the user has already sent a request for*/
                            String[] requestedUsernames = new String[0];
                            try {
                                requestedUsernames = new JSONObject(resultFormPost.submitPost(resultRequestPageRequestResults, MethodType.POST)).getString("names").split("\n");
                            } catch (JSONException e) {}

                            setRemovableUsernames(requestedUsernames); //Callback function
                        }
                    });

                    threadRequestedUsernames.start();
                    threadRequestedUsernames.join();

                    /*Need to do this because you can not alter adapter in a thread that is not in UI*/

                    for(String cluster : removableUsernames) {
                        if(!cluster.isEmpty()) {
                            listItems.remove(FragmentUtils.returnParsedUsernameCluster(cluster));
                        }
                    }

                    Thread threadReceivedUsernames = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /*Remove Results that the user has already sent a request for*/
                            String[] requestedUsernames = new String[0];
                            try {
                                requestedUsernames = new JSONObject(resultFormPost.submitPost(resultReceivedRequestPageRequestResults, MethodType.POST)).getString("names").split("\n");
                            } catch (JSONException e) {}

                            setRemovableUsernames(requestedUsernames); //Callback function
                        }
                    });

                    threadReceivedUsernames.start();
                    threadReceivedUsernames.join();

                    /*Need to do this because you can not alter adapter in a thread that is not in UI*/

                    for(String cluster : removableUsernames) {
                        if(!cluster.isEmpty()) {
                            listItems.remove(FragmentUtils.returnParsedUsernameCluster(cluster));
                        }
                    }

                    for(String connection : getIntent().getStringArrayExtra("connections")) {
                        listItems.remove(FragmentUtils.returnParsedUsernameCluster(connection));
                    }

                    adapter.notifyDataSetChanged();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final int pos = (int) id;

                            /*FROM: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android*/
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which){

                                        case DialogInterface.BUTTON_POSITIVE:

                                            resultFormPost.addPair("requested", listItems.get((int)pos));

                                            Thread thread = new Thread(new Runnable(){
                                                @Override
                                                public void run() {
                                                    resultFormPost.submitPost(resultRequestPageRequest, MethodType.POST);
                                                }
                                            });

                                            try {
                                                thread.start();
                                                thread.join();

                                                Toast.makeText(ConnectionAdd.this, "Request send to: " + listItems.get(pos),
                                                        Toast.LENGTH_LONG).show();

                                                Intent dataIntent = new Intent();

                                                dataIntent.putExtra("userRequested", listItems.get(pos));
                                                ConnectionAdd.this.setResult(200, dataIntent); //This helps populate the other fragments

                                                ConnectionAdd.this.finish();

                                            } catch (Exception e) {
                                                //TODO
                                            }

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            /*FROM: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionAdd.this);
                            builder.setMessage("Send Request?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                        }

                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(ConnectionAdd.this, (resultsFound[0] ? "Results found: "
                                                    + listItems.size() : "No Results Found"),
                                                    Toast.LENGTH_LONG).show();

            }

        });

        btnAddConnectionReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setRemovableUsernames(String[] removableUsernames) {
        this.removableUsernames = removableUsernames;
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        introVideoView.start();
    } */

}