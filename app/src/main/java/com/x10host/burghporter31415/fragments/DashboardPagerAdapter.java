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

        }/* else if(i == 1) {

            CoordinatesFragment coordinatesFragment = new CoordinatesFragment();
            coordinatesFragment.setArguments(this.bundle);

            return coordinatesFragment;

        } */else {

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(this.bundle);

            return mapFragment;

        }

    }

    @Override
    public int getCount() {
        return 2; //Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return mContext.getString(R.string.category_entry);
            case 1:
                return mContext.getString(R.string.category_map);
            /*case 2:
                return mContext.getString(R.string.category_map);*/
            default:
                return null;

        }
    }

    public void setData(String[] newData) {
        this.bundle.putStringArray("arr", newData);
    }

    public void setBundle(Bundle bundle) { this.bundle = bundle; }

}
