package com.x10host.burghporter31415.internetservices;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.x10host.burghporter31415.fileactions.File;

public class BackgroundServiceBroadcast extends BroadcastReceiver {

    private final String FILE_NAME="Silent_Voyager_Credentials.txt";

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intentService = new Intent(context, BackgroundUploadService.class);

        com.x10host.burghporter31415.fileactions.File file = new com.x10host.burghporter31415.fileactions.File(FILE_NAME);

        /*Read the stored credentials from the file*/
        String[] results = file.readFile(context, FILE_NAME).split("\n");

        intentService.putExtra("username", results[0].trim());
        intentService.putExtra("password", results[1].trim());

        context.startForegroundService(intentService);

    }
}
