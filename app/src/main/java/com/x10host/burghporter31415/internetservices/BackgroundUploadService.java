package com.x10host.burghporter31415.internetservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.x10host.burghporter31415.silentvoyager.MainActivity;
import com.x10host.burghporter31415.silentvoyager.R;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;
import com.x10host.burghporter31415.webconnector.GPSLocation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BackgroundUploadService extends Service {

    private static final String TAG = "com.x10host";
    private PendingIntent data;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10000; //Retrieve the location every 10 seconds.
    private static final float LOCATION_DISTANCE = 4; //Minimum location difference should be 4 meters

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Bundle extras = intent.getExtras();

        data = intent.getParcelableExtra("pendingIntent");

        final String BASE_URL = extras.getString("BASE_URL");
        final String RELATIVE_URL = extras.getString("RELATIVE_URL");

        /*Do not send in POST*/
        extras.remove("BASE_URL");
        extras.remove("RELATIVE_URL");
        extras.remove("pendingIntent");

        /*This will be an indefinite background process*/

        /*ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate( new Runnable() {
            @Override
            public void run() {

                synchronized(this) {

                    /*Intent resultIntent = new Intent();

                    FormPost<String, String> connection = new FormPost<String, String>();
                    PHPPage page = new PHPPage(BASE_URL, RELATIVE_URL);

                    Pair<Double, Double> pair;

                    try {

                        while(true) {

                            GPSLocation location = new GPSLocation(BackgroundUploadService.this);

                            for (String key : extras.keySet()) {
                                //Log.i(TAG, key);
                                connection.addPair(key, extras.getString(key));
                                resultIntent.putExtra(key, extras.getString(key));
                            }

                            pair = location.getCoords();

                            connection.addPair("latitude", Double.toString(pair.first));
                            connection.addPair("longitude", Double.toString(pair.second));
                            connection.addPair("city", location.getCity());

                            String result = connection.submitPost(page, MethodType.POST);

                            resultIntent.putExtra("result", result);


                            //Log.i(TAG, String.valueOf(pair.first));
                            //Log.i(TAG, String.valueOf(pair.second));
                            //Log.i(TAG, location.getCity());

                            /*Use BroadCast Receiver Instead*/
                            /*dataCity.send(BackgroundUploadService.this, 200, resultIntent);
                            dataCoordinates.send(BackgroundUploadService.this, 200, resultIntent);
                            dataMap.send(BackgroundUploadService.this, 200, resultIntent);
                        }

                    } catch(Exception e) {
                        Log.i(TAG, "Background Service Failed");
                        Log.i(TAG, e.toString());
                    }


                }
            }
        }, 0, 5, TimeUnit.SECONDS); */

        /*If interrupted by the system, restart the service. Perhaps I could just use a Foreground service?*/
        return Service.START_STICKY;

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        int NOTIFICATION_ID = (int) (System.currentTimeMillis()%10000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
            showTaskIntent.setAction(Intent.ACTION_MAIN);
            showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    showTaskIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(contentIntent)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        }

        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        }
    }

}

