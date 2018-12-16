package com.x10host.burghporter31415.internetservices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.x10host.burghporter31415.fileactions.File;
import com.x10host.burghporter31415.silentvoyager.MainActivity;
import com.x10host.burghporter31415.silentvoyager.R;
import com.x10host.burghporter31415.webconnector.ConnectionTest;
import com.x10host.burghporter31415.webconnector.FormPost;
import com.x10host.burghporter31415.webconnector.MethodType;
import com.x10host.burghporter31415.webconnector.PHPPage;
import com.x10host.burghporter31415.webconnector.GPSLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BackgroundUploadService extends Service {

    private static final String TAG = "com.x10host";
    private static final String FILE_NAME = "Silent_Voyager_Coordinates_Offline.txt";
    private com.x10host.burghporter31415.fileactions.File file;

    private Intent currentIntent = null;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 2*60*1000; //Retrieve the location every 2 minutes
    private static final float LOCATION_DISTANCE = 30; //Minimum location difference should be 30 meters

    private final String[] columns = {"latitude", "longitude", "altitude", "city", "datestamp", "year", "month", "day","hour", "minute", "second"};

    private FormPost<String, String> connection = null;
    private PHPPage page = null;

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

            final Location tempLocation = location;

            if(currentIntent != null && connection != null && page != null) {


                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        synchronized (this) {


                            try {

                                /*Get the City for the coordinates*/
                                String cityName = "";
                                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses;

                                try {

                                    addresses = gcd.getFromLocation(tempLocation.getLatitude(), tempLocation.getLongitude(), 1);
                                    if (addresses.size() > 0) { cityName = addresses.get(0).getLocality(); }

                                } catch (Exception e) {/*Will handle later*/}


                                /*Need the datestamp for every upload*/
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                                String dateStamp = sdf.format(new Date());
                                String[] currentDateAndTime = dateStamp.split("_");

                                String result = "";
                                boolean isNetworkAvailable = ConnectionTest.isNetworkAvailable(getApplicationContext());

                                if(isNetworkAvailable) {

                                    /*Upload all the offline coordinates from the stored file if there are entries*/
                                    String[] coords = file.readFile(getApplicationContext()).trim().split("\n");

                                    if(!(coords[0] == null || coords[0].equals(""))) {

                                        for (String coord : coords) {

                                            String[] data = coord.split(" ");

                                            int i = 0;
                                            for(String key : columns) {
                                                connection.addPair(key, data[i++]);
                                            }

                                            result = connection.submitPost(page, MethodType.POST);
                                        }

                                        file.deleteFile(getApplicationContext(), FILE_NAME);
                                        file = new com.x10host.burghporter31415.fileactions.File(FILE_NAME);

                                    }


                                }

                                /*Upload current data to either file or database depending on if connection is available*/
                                connection.addPair(columns[0], String.valueOf(tempLocation.getLatitude()));
                                connection.addPair(columns[1], String.valueOf(tempLocation.getLongitude()));
                                connection.addPair(columns[2], String.valueOf(tempLocation.getAltitude()));
                                connection.addPair(columns[3], cityName);
                                connection.addPair(columns[4], dateStamp);

                                connection.addPair(columns[5], currentDateAndTime[0]);
                                connection.addPair(columns[6], currentDateAndTime[1]);
                                connection.addPair(columns[7], currentDateAndTime[2]);
                                connection.addPair(columns[8], currentDateAndTime[3]);
                                connection.addPair(columns[9], currentDateAndTime[4]);
                                connection.addPair(columns[10], currentDateAndTime[5]);

                                if(!isNetworkAvailable) {

                                    HashMap<String, String> map = connection.getPairs();

                                    String[] values = { map.get("latitude"), map.get("longitude"),map.get("altitude"),map.get("city"),map.get("datestamp"),
                                                        map.get("year"),map.get("month"),map.get("day"),map.get("hour"),map.get("minute"),map.get("second")};

                                    file.writeToFileSingleLine(getApplicationContext(), values);

                                } else {
                                    result = connection.submitPost(page, MethodType.POST);
                                }

                            } catch(Exception e) {Log.i("com.x10host", e.toString());}
                        }
                    }
                };

                Thread thread = new Thread(r);
                thread.start();
            }
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

        this.currentIntent = intent;
        final Bundle extras = intent.getExtras();

        final String BASE_URL = extras.getString("BASE_URL");
        final String RELATIVE_URL = extras.getString("RELATIVE_URL");

        /*This will be an indefinite background process*/

        Runnable r = new Runnable() {
            @Override
            public void run() {

                synchronized(this) {


                    try {

                        String BASE_URL = extras.getString("BASE_URL");
                        String RELATIVE_URL = extras.getString("RELATIVE_URL");

                        /*Do not send in POST*/
                        extras.remove("BASE_URL");
                        extras.remove("RELATIVE_URL");

                        connection = new FormPost<String, String>();

                        page = new PHPPage(BASE_URL, RELATIVE_URL);

                        for(String key : extras.keySet()) {
                            connection.addPair(key, extras.getString(key));
                        }

                        file = new com.x10host.burghporter31415.fileactions.File(FILE_NAME);

                        //String result = connection.submitPost(page, MethodType.POST);

                    } catch(Exception e) {
                        Log.i(TAG, "Background Service Failed");
                    }

                }
            }
        };

        Thread thread = new Thread(r);
        thread.start();

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
        }
        catch (java.lang.SecurityException ex) {  Log.i(TAG, "fail to request location update, ignore", ex); }
        catch (IllegalArgumentException ex) { Log.d(TAG, "network provider does not exist, " + ex.getMessage()); }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        }
        catch (java.lang.SecurityException ex) {  Log.i(TAG, "fail to request location update, ignore", ex); }
        catch (IllegalArgumentException ex) { Log.d(TAG, "gps provider does not exist " + ex.getMessage()); }
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

