package com.githug.florent37.materialviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.githug.florent37.materialviewpager.fragment.ListFragment;
import com.githug.florent37.materialviewpager.fragment.WebViewFragment;
import com.squareup.picasso.Picasso;

public class MainActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    private PagerSlidingTabStrip mPagerTitleStrip;

    private ImageView headerBackgroundImage;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerTitleStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerTitleStrip);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        headerBackgroundImage = (ImageView) findViewById(R.id.headerBackgroundImage);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);

        Picasso.with(getApplicationContext()).load("https://dancole2009.files.wordpress.com/2010/01/material-testing-81.jpg").centerCrop().fit().into(headerBackgroundImage);

        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    //case 0:
                    //    return WebViewFragment.newInstance();
                    default:
                        return ListFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        });
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());

        mPagerTitleStrip.setViewPager(mViewPager);

        MaterialViewPager.register(this, new MaterialViewPagerAnimator(
                200,
                true,
                MaterialViewPagerHeader
                        .withToolbar(toolbar)
                        .withPagerSlidingTabStrip(mPagerTitleStrip)
                        .withHeaderBackground(findViewById(R.id.headerBackground))
                        .withStatusBackground(findViewById(R.id.statusBackground))
                        .withLogo(findViewById(R.id.logo_white))
        ));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

}
