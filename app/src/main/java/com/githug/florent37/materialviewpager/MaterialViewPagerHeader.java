package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.astuetz.PagerSlidingTabStrip;

import static com.githug.florent37.materialviewpager.Utils.dpToPx;

/**
 * Created by florentchampigny on 25/04/15.
 */
public class MaterialViewPagerHeader {

    protected Context context;

    protected Toolbar toolbar;
    protected PagerSlidingTabStrip mPagerSlidingTabStrip;

    protected View headerBackground;
    protected View statusBackground;
    protected View mLogo;

    public float finalTitleY;
    public float finalTabsY;
    public float finalTitleX;
    public float originalTitleY;
    public float originalTitleX;
    public float finalScale;

    public MaterialViewPagerHeader(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.context = toolbar.getContext();
    }

    public Context getContext() {
        return context;
    }

    public MaterialViewPagerHeader setContext(Context context) {
        this.context = context;
        return this;
    }

    public MaterialViewPagerHeader setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        return this;
    }

    public MaterialViewPagerHeader setPagerSlidingTabStrip(PagerSlidingTabStrip pagerSlidingTabStrip) {
        this.mPagerSlidingTabStrip = pagerSlidingTabStrip;

        mPagerSlidingTabStrip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                finalTabsY = dpToPx(-2, context);

                mPagerSlidingTabStrip.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });

        return this;
    }

    public MaterialViewPagerHeader setHeaderBackground(View headerBackground) {
        this.headerBackground = headerBackground;
        return this;
    }

    public MaterialViewPagerHeader setStatusBackground(View statusBackground) {
        this.statusBackground = statusBackground;
        return this;
    }

    public MaterialViewPagerHeader setLogo(View logo) {
        this.mLogo = logo;
        mLogo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                finalTitleY = dpToPx(35f, context);
                finalTitleX = dpToPx(18f, context);
                originalTitleY = mLogo.getY();
                originalTitleX = mLogo.getX();

                mLogo.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        return this;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public PagerSlidingTabStrip getPagerSlidingTabStrip() {
        return mPagerSlidingTabStrip;
    }

    public View getHeaderBackground() {
        return headerBackground;
    }

    public View getStatusBackground() {
        return statusBackground;
    }

    public View getmLogo() {
        return mLogo;
    }
}
