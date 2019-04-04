package com.x10host.burghporter31415.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.x10host.burghporter31415.silentvoyager.R;

public class DashboardPagerAdapter extends FragmentPagerAdapter  {

    private Context mContext;
    private Bundle bundle;
    private String[] data;

    public DashboardPagerAdapter(Context context, FragmentManager fm) {

        super(fm);
        mContext = context;
        bundle = new Bundle();

    }

    @Override
    public Fragment getItem(int i) {

        if(i == 0) {

            EntryFragment entryFragment = new EntryFragment();
            entryFragment.setArguments(this.bundle);

            return entryFragment;

        } else if(i == 1) {

            ConnectionsFragment connectionsFragment = new ConnectionsFragment();
            connectionsFragment.setArguments(this.bundle);

            return connectionsFragment;

        } else if(i == 2) {

            RequestsFragment requestsFragment = new RequestsFragment();
            requestsFragment.setArguments(this.bundle);

            return requestsFragment;

        } else {

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(this.bundle);

            return mapFragment;

        }

    }

    @Override
    public int getCount() {
        return 4; //Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return mContext.getString(R.string.category_entry);
            case 1:
                return mContext.getString(R.string.category_connections);
            case 2:
                return mContext.getString(R.string.category_requests);
            case 3:
                return mContext.getString(R.string.category_map);
            default:
                return null;

        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setData(String[] results, String[] connectionResults, String[] requestResults) {
        this.bundle.putStringArray("results", results);
        this.bundle.putStringArray("connectionResults", connectionResults);
        this.bundle.putStringArray("requestResults", requestResults);
    }

    public String[] getResults() {
        return this.bundle.getStringArray("results");
    }

    public String[] getConnectionResults() {
        return this.bundle.getStringArray("connectionResults");
    }

    public String[] getRequestResults() {
        return this.bundle.getStringArray("requestResults");
    }

    public void setBundle(Bundle bundle) { this.bundle = bundle; }

}
