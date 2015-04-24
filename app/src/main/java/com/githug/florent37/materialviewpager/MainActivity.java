package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.githug.florent37.materialviewpager.Utils.dpToPx;


public class MainActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private List<Object> mContentItems = new ArrayList<>();

    private Toolbar toolbar;
    private View headerBackground;
    private ImageView headerBackgroundImage;
    private View toolbarBackground;
    private View statusBackground;

    private View logo_white;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, 0, 0);
        mDrawer.setDrawerListener(mDrawerToggle);

        for (int i = 0; i < 100; ++i)
            mContentItems.add(new Object());

        headerBackground = findViewById(R.id.headerBackground);
        headerBackgroundImage = (ImageView) findViewById(R.id.headerBackgroundImage);
        toolbarBackground = findViewById(R.id.toolbarBackground);
        statusBackground = findViewById(R.id.statusBackground);
        logo_white = findViewById(R.id.logo_white);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new HomeListArticleMaterialAdapter(this, mContentItems);
        mRecyclerView.setAdapter(mAdapter);

        //getSupportActionBar().setTitle("Les titres");

        toolbarBackground.setAlpha(0);
        statusBackground.setAlpha(0);

        logo_white.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                listenScroll();
                logo_white.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        Picasso.with(getApplicationContext()).load("https://dancole2009.files.wordpress.com/2010/01/material-testing-81.jpg").centerCrop().fit().into(headerBackgroundImage);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void listenScroll() {

        final float finalTitleY = dpToPx(35f, this);
        final float finalTitleX = dpToPx(18f, this);
        final float originalTitleY = logo_white.getY();
        final float originalTitleX = logo_white.getX();

        final float finalScale = 0.6f;

        final float heightMaxScrollToolbar = dpToPx(250f, this);

        final float elevation = dpToPx(4, this);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int totalScrolled = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrolled += dy;

                headerBackground.setTranslationY(-totalScrolled / 1.5f);

                float percent = totalScrolled / heightMaxScrollToolbar;
                percent = Math.min(percent,1);
                {
                    toolbarBackground.setAlpha(percent);
                    statusBackground.setAlpha(percent);

                    if (percent == 1) {
                        ViewCompat.setElevation(toolbarBackground, elevation);
                        ViewCompat.setElevation(toolbar, elevation);
                    } else {
                        ViewCompat.setElevation(toolbarBackground, 0);
                        ViewCompat.setElevation(toolbar, 0);
                    }

                    logo_white.setTranslationY((finalTitleY - originalTitleY) * percent);
                    logo_white.setTranslationX((finalTitleX - originalTitleX) * percent);

                    float scale = (1 - percent) * (1 - finalScale) + finalScale;

                    logo_white.setScaleX(scale);
                    logo_white.setScaleY(scale);
                }
            }
        });

    }


    public class HomeListArticleMaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        Context context;
        List<Object> contents;

        static final int TYPE_PLACEHOLDER = 0;
        static final int TYPE_HEADER = 1;
        static final int TYPE_CELL = 2;

        public HomeListArticleMaterialAdapter(Context context, List<Object> contents) {
            this.context = context;
            this.contents = contents;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return TYPE_PLACEHOLDER;
                case 1:
                    return TYPE_HEADER;
                default:
                    return TYPE_CELL;
            }
        }

        @Override
        public int getItemCount() {
            return contents.size() + 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            switch (viewType) {
                case TYPE_PLACEHOLDER: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.activity_home_material_placeholder, parent, false);
                    return new RecyclerView.ViewHolder(view) {
                    };
                }
                case TYPE_HEADER: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_card_big, parent, false);
                    return new RecyclerView.ViewHolder(view) {
                    };
                }
                case TYPE_CELL: {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_card_small, parent, false);
                    return new RecyclerView.ViewHolder(view) {
                    };
                }
            }
            return null;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_PLACEHOLDER:
                    break;
                case TYPE_HEADER:
                    break;
                case TYPE_CELL:
                    break;
            }
        }
    }

}
