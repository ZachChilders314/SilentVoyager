package com.x10host.burghporter31415.webconnector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import com.x10host.burghporter31415.internetservices.BackgroundUploadService;
import com.x10host.burghporter31415.silentvoyager.MainActivity;

import java.util.List;
import java.util.Locale;

public class GPSLocation  {

    private static final String TAG = "com.x10host";

    LocationManager locationManager;
    Context mContext;
    Location currentLoc;

    public GPSLocation(Context mContext) {
        LocationListener listener;

        this.mContext = mContext;
        checkPermission();

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        currentLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                currentLoc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
    }

    public Pair<Double, Double> getCoords() {

        try {

            checkPermission();
            return new Pair<Double, Double>(currentLoc.getLatitude(), currentLoc.getLongitude());

        } catch (Exception e) { Log.i(TAG, e.toString()); }

        return null;
    }

    /*https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android*/
    public String getCity() {

        checkPermission();
        /*------- To get city name from coordinates -------- */
        String cityName = "";
        Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());
        List<Address> addresses;

        Location currentLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        try {
            addresses = gcd.getFromLocation(currentLoc.getLatitude(),
                    currentLoc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        } catch (Exception e) {/*Will handle later*/}

        return cityName;
    }

    private void checkPermission () {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }
}
