package com.x10host.burghporter31415.silentvoyager;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.x10host.burghporter31415.fragments.DashboardPagerAdapter;

public class Dashboard extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewPager = (ViewPager) findViewById(R.id.dashboardViewpager);

        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.dashboardTab);
        tabLayout.setupWithViewPager(viewPager);

    }
}
