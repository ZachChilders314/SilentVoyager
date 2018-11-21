package com.x10host.burghporter31415.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;

public class InternetPHPRequestService extends Service {

    public InternetPHPRequestService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                
                synchronized(this) {
                    try {

                        String username = txtUsername.getText().toString();
                        String password = txtPassword.getText().toString();

                        FormPost<String, String> connection = new FormPost<String, String>();

                        PHPPage page = new PHPPage(BASE_URL, RELATIVE_URL);

                        connection.addPair("username", username);
                        connection.addPair("password", password);

                        String result = connection.submitPost(page, MethodType.POST);

                    } catch(Exception e) {

                    }
                }
            }
        };

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
