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

public class InternetPHPRequestService extends Service {

    private static final String TAG = "com.x10host";

    private PendingIntent data;

    public InternetPHPRequestService() {

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

                        String BASE_URL = extras.getString("BASE_URL");
                        String RELATIVE_URL = extras.getString("RELATIVE_URL");

                        /*Do not send in POST*/
                        extras.remove("BASE_URL");
                        extras.remove("RELATIVE_URL");
                        extras.remove("pendingIntent");

                        FormPost<String, String> connection = new FormPost<String, String>();

                        PHPPage page = new PHPPage(BASE_URL, RELATIVE_URL);

                        for(String key : extras.keySet()) {
                            Log.i(TAG, key);
                            connection.addPair(key, extras.getString(key));
                            resultIntent.putExtra(key, extras.getString(key));
                        }

                        String result = connection.submitPost(page, MethodType.POST);

                        resultIntent.putExtra("result", result);

                        data.send(InternetPHPRequestService.this, 200, resultIntent);

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
