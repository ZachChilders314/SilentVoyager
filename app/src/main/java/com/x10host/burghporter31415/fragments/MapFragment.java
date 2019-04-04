package com.x10host.burghporter31415.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.x10host.burghporter31415.silentvoyager.R;

public class MapFragment extends Fragment implements Broadcastable, OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private String[] arr;

    public MapFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        final Bundle bundle = this.getArguments();
        this.arr = bundle.getStringArray("results");

        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void broadcastData(Bundle data) {
        //TODO
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

        if(arr == null || arr.length == 0) {return; }

        String[] currentCoords = this.arr[0].split(",");
        LatLng coords = new LatLng(Double.parseDouble(currentCoords[1]), Double.parseDouble(currentCoords[2]));
        map.setMyLocationEnabled(true); //Already check the necessary permissions on login
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, (float)16.0));

        /*POLYLINES*/
        PolygonOptions rectOptions = new PolygonOptions();

        for(int i = 0; i < (Math.min(50, this.arr.length)); i++) {
            String[] components = this.arr[i].split(",");
            rectOptions.add(new LatLng(Double.parseDouble(components[1]),
                                        Double.parseDouble(components[2])));

            Marker marker = map.addMarker(new MarkerOptions()
            .position(new LatLng(Double.parseDouble(components[1]), Double.parseDouble(components[2])))
            .title(i == 0 ? "Last Position" : i == this.arr.length - 1 ? "Start Position" : "Position: "
                    + (this.arr.length - i)));

            if(i == 0) {marker.showInfoWindow();}


        }

        Polygon polygon = map.addPolygon(rectOptions);
        polygon.setStrokeColor(Color.parseColor("#FFFFFF"));

        boolean success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getContext(), R.raw.map_style));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, (float)16.0));
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID); OVERRIDES JSON FILE

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
