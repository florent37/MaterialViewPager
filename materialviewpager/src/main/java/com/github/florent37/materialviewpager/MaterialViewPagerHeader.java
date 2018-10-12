package com.github.florent37.materialviewpager;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import android.support.v4.view.ViewCompat;

import static com.github.florent37.materialviewpager.Utils.dpToPx;

/**
 * Created by florentchampigny on 25/04/15.
 * A class containing references to views inside MaterialViewPager's header
 */
public class MaterialViewPagerHeader {

    protected Context context;

    protected View toolbarLayout;
    protected Toolbar toolbar;
    protected View mPagerSlidingTabStrip;

    protected View toolbarLayoutBackground;
    protected View headerBackground;
    protected View statusBackground;
    protected View mLogo;

    //positions used to animate views during scroll

    public float finalTabsY;

    public float finalTitleY;
    public float finalTitleHeight;
    public float finalTitleX;

    public float originalTitleY;
    public float originalTitleHeight;
    public float originalTitleX;
    public float finalScale;

    private MaterialViewPagerHeader(Toolbar toolbar) {
        this.toolbar = toolbar;
        this.context = toolbar.getContext();
        this.toolbarLayout = (View) toolbar.getParent();
    }

    public static MaterialViewPagerHeader withToolbar(Toolbar toolbar) {
        return new MaterialViewPagerHeader(toolbar);
    }

    public Context getContext() {
        return context;
    }

    public MaterialViewPagerHeader withPagerSlidingTabStrip(View pagerSlidingTabStrip) {
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

    public MaterialViewPagerHeader withHeaderBackground(View headerBackground) {
        this.headerBackground = headerBackground;
        return this;
    }

    public MaterialViewPagerHeader withStatusBackground(View statusBackground) {
        this.statusBackground = statusBackground;
        return this;
    }

    public MaterialViewPagerHeader withToolbarLayoutBackground(View toolbarLayoutBackground) {
        this.toolbarLayoutBackground = toolbarLayoutBackground;
        return this;
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public MaterialViewPagerHeader withLogo(View logo) {
        this.mLogo = logo;

        //when logo get a height, initialise initial & final logo positions
        toolbarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //rotation fix, if not set, originalTitleY = Na
                ViewCompat.setTranslationY(mLogo,0);
                ViewCompat.setTranslationX(mLogo, 0);

                originalTitleY = ViewCompat.getY(mLogo);
                originalTitleX = ViewCompat.getX(mLogo);

                originalTitleHeight = mLogo.getHeight();
                finalTitleHeight = dpToPx(21, context);

                //the final scale of the logo
                finalScale = finalTitleHeight / originalTitleHeight;

                finalTitleY = (toolbar.getPaddingTop() + toolbar.getHeight()) / 2 - finalTitleHeight / 2 - (1 - finalScale) * finalTitleHeight;

                //(mLogo.getWidth()/2) *(1-finalScale) is the margin left added by the scale() on the logo
                //when logo scaledown, the content stay in center, so we have to anually remove the left padding
                finalTitleX = dpToPx(52f, context) - (mLogo.getWidth() / 2) * (1 - finalScale);

                toolbarLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        return this;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public View getHeaderBackground() {
        return headerBackground;
    }

    public View getStatusBackground() {
        return statusBackground;
    }

    public View getLogo() {
        return mLogo;
    }

}
