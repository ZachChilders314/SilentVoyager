package com.x10host.burghporter31415.internetservices;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

public class InternetPHPRequestServiceLogin extends Service {

    private static final String TAG = "com.x10host";

    private PendingIntent data;

    public InternetPHPRequestServiceLogin() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Bundle extras = intent.getExtras();
        data = intent.getParcelableExtra("pendingIntent");

        Runnable r = new Runnable() {
            @Override
            public void run() {

                synchronized(this) {

                    Intent resultIntent = new Intent();

                    try {

                        String username = extras.getString("username");
                        String password = extras.getString("password");

                        String BASE_URL = extras.getString("BASE_URL");
                        String RELATIVE_URL = extras.getString("RELATIVE_URL");

                        FormPost<String, String> connection = new FormPost<String, String>();

                        PHPPage page = new PHPPage(BASE_URL, RELATIVE_URL);

                        connection.addPair("username", username);
                        connection.addPair("password", password);

                        String result = connection.submitPost(page, MethodType.POST);

                        resultIntent.putExtra("result", result);
                        resultIntent.putExtra("username", username);
                        resultIntent.putExtra("password", password);

                        data.send(InternetPHPRequestServiceLogin.this, 200, resultIntent);

                    } catch(Exception e) {
                        Log.i(TAG, "Failed");
                    }


                }
            }
        };

        Thread thread = new Thread(r);
        thread.start();

        return Service.START_STICKY;

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
