package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.HashMap;
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

        if (yOffset > 250)
            yOffset = 250;
        if(yOffset < 0)
            yOffset = 0;
        Log.d("yOffset", "" + yOffset);

        for (Object scroll : scrollViewList) {
            if (scroll != source) {
                calledScrollList.add(scroll);

                if (scroll instanceof RecyclerView) {
                    RecyclerView.LayoutManager layoutManager = ((RecyclerView) scroll).getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        linearLayoutManager.scrollToPositionWithOffset(0, -yOffset);
                    }
                } else if (scroll instanceof WebView) {
                    ((WebView) scroll).scrollTo(0, yOffset);
                }

                yOffsets.put(scroll, yOffset);

                calledScrollList.remove(scroll);
            }
        }

        {
            float newY = headerBackground.getY() + (-yOffset / 1.5f);
            if (newY <= 0)
                headerBackground.setTranslationY(-yOffset / 1.5f);
        }


        float percent = yOffset / heightMaxScrollToolbar;
        percent = Math.max(0, Math.min(percent, 1));
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
                float newY = mPagerSlidingTabStrip.getY() + -yOffset;
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

    private List<Object> scrollViewList = new ArrayList<>();
    private List<Object> calledScrollList = new ArrayList<>();
    private HashMap<Object, Integer> yOffsets = new HashMap<>();

    public void registerRecyclerView(final RecyclerView recyclerView, final RecyclerView.OnScrollListener onScrollListener) {
        if (recyclerView != null) {
            scrollViewList.add(recyclerView);
            yOffsets.put(recyclerView, 0);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int scrollY = yOffsets.get(recyclerView);

                    if (calledScrollList.contains(recyclerView)) {
                        calledScrollList.remove(recyclerView);
                        return;
                    }

                    scrollY += dy;
                    yOffsets.put(recyclerView, scrollY);

                    onMaterialScrolled(recyclerView, scrollY);
                }
            });
        }
    }

    public void registerWebView(final ObservableWebView webView, Object o) {
        if (webView != null) {
            scrollViewList.add(webView);
            webView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (calledScrollList.contains(webView)) {
                        calledScrollList.remove(webView);
                        return;
                    }
                    onMaterialScrolled(webView, i);
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
}
