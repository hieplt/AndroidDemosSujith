package com.hydapps.androiddemossujith;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

public class BottomNavigationDemo extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private BottomNavigationView mBottomNav;
    private ViewPager mViewPager;
    private static final String TAG = "BottomNavigationDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_navigation_demo);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mBottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav) ;
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fr = new DisplayFragment();
                Bundle b = new Bundle();
                b.putInt("tab", position);
                fr.setArguments(b);
                return fr;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        mViewPager.setAdapter(adapter);


        mBottomNav.setOnNavigationItemSelectedListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab1:
                mViewPager.setCurrentItem(0);
                return true;
            case R.id.tab2:
                mViewPager.setCurrentItem(1);
                return true;
            case R.id.tab3:
                mViewPager.setCurrentItem(2);
                return true;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position < 3) {
            ((ViewGroup) mBottomNav.getChildAt(0)).getChildAt(position).callOnClick();
        } else {
            Log.wtf(TAG, "onPageSelected: " + position);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
