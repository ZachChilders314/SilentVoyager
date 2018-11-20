package com.x10host.burghporter31415.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x10host.burghporter31415.silentvoyager.R;

public class CoordinatesFragment extends Fragment {

    public CoordinatesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coordinates, container, false);
        return rootView;
    }
}
