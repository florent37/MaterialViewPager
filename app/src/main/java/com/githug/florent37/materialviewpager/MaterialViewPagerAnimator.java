package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.List;

import static com.githug.florent37.materialviewpager.Utils.colorWithAlpha;
import static com.githug.florent37.materialviewpager.Utils.dpToPx;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class MaterialViewPagerAnimator {

    private Toolbar toolbar;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    private View headerBackground;
    private View statusBackground;
    private View mLogo;

    private Context context;

    private int color;

    float finalTitleY;

    float finalTabsY;

    float finalTitleX;
    float originalTitleY;
    float originalTitleX;
    float finalScale;
    float heightMaxScrollToolbar;
    float elevation;


    public MaterialViewPagerAnimator(Toolbar toolbar, PagerSlidingTabStrip pagerSlidingTabStrip, View headerBackground, View statusBackground, View logo_white) {
        this.context = toolbar.getContext();

        color = context.getResources().getColor(R.color.colorPrimary);

        this.toolbar = toolbar;
        this.mPagerSlidingTabStrip = pagerSlidingTabStrip;
        this.headerBackground = headerBackground;
        this.statusBackground = statusBackground;
        this.mLogo = logo_white;

        mPagerSlidingTabStrip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                initialise();
                mPagerSlidingTabStrip.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
    }

    private void initialise() {
        finalTitleY = dpToPx(35f, context);
        finalTitleX = dpToPx(18f, context);
        originalTitleY = mLogo.getY();
        originalTitleX = mLogo.getX();

        finalTabsY = dpToPx(0, context);

        finalScale = 0.6f;

        heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);

        elevation = dpToPx(4, context);
    }

    public void onMaterialScrolled(Object source, int yOffset) {

        for (RecyclerView r : recyclerViewList) {
            if (r != source) {
                calledRecyclerViewList.add(r);
                r.scrollBy(0, yOffset);
            }
        }

        {
            float newY = headerBackground.getY() + (-yOffset / 1.5f);
            if (newY <= 0)
                headerBackground.setTranslationY(-yOffset / 1.5f);
        }

        float percent = yOffset / heightMaxScrollToolbar;
        percent = Math.min(percent, 1);
        {
            int newColor = colorWithAlpha(color, percent);

            toolbar.setBackgroundColor(newColor);
            mPagerSlidingTabStrip.setBackgroundColor(newColor);
            statusBackground.setBackgroundColor(newColor);


            if (percent == 1) {
                ViewCompat.setElevation(toolbar, elevation);
                ViewCompat.setElevation(mPagerSlidingTabStrip, elevation);
            } else {
                ViewCompat.setElevation(toolbar, 0);
                ViewCompat.setElevation(mPagerSlidingTabStrip, 0);
            }

            {
                float newY = mPagerSlidingTabStrip.getY() - yOffset;
                if (newY >= finalTabsY)
                    mPagerSlidingTabStrip.setTranslationY(-yOffset);
            }

            mLogo.setTranslationY((finalTitleY - originalTitleY) * percent);
            mLogo.setTranslationX((finalTitleX - originalTitleX) * percent);

            float scale = (1 - percent) * (1 - finalScale) + finalScale;

            mLogo.setScaleX(scale);
            mLogo.setScaleY(scale);
        }
    }

    private List<ObservableWebView> webViews = new ArrayList<>();
    private List<RecyclerView> recyclerViewList = new ArrayList<>();
    private List<RecyclerView> calledRecyclerViewList = new ArrayList<>();
    private int totalScrolled = 0;

    public void registerRecyclerView(final RecyclerView recyclerView, final RecyclerView.OnScrollListener onScrollListener) {
        if (recyclerView != null) {
            recyclerViewList.add(recyclerView);
            recyclerView.setClipToPadding(false);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (calledRecyclerViewList.contains(recyclerView)) {
                        calledRecyclerViewList.remove(recyclerView);
                        return;
                    }

                    totalScrolled += dy;

                    onMaterialScrolled(recyclerView,totalScrolled);

                    if (onScrollListener != null)
                        onScrollListener.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (onScrollListener != null)
                        onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            });
        }
    }

    public void registerWebView(final ObservableWebView webView, Object o) {
        webViews.add(webView);

        webView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int i, boolean b, boolean b2) {
                onMaterialScrolled(webView,totalScrolled);
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {

            }
        });
    }
}
