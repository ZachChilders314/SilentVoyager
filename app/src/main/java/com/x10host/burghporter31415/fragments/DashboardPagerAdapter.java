package com.x10host.burghporter31415.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.x10host.burghporter31415.silentvoyager.R;

public class DashboardPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public DashboardPagerAdapter(Context context, FragmentManager fm) {

        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int i) {

        if(i == 0) {
            return new CityFragment();
        } else if(i == 1) {
            return new CoordinatesFragment();
        } else {
            return new MapFragment();
        }

    }

    @Override
    public int getCount() {
        return 3; //Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return mContext.getString(R.string.category_city);
            case 1:
                return mContext.getString(R.string.category_coordinates);
            case 2:
                return mContext.getString(R.string.category_map);
            default:
                return null;

        }
    }

}
