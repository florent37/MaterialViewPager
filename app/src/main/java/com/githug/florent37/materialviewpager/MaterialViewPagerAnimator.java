package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
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
    //float heightMaxScrollToolbar;
    float elevation;

    float scrollMax;


    public MaterialViewPagerAnimator(Toolbar toolbar, PagerSlidingTabStrip pagerSlidingTabStrip, View headerBackground, View statusBackground, View logo_white) {
        this.context = toolbar.getContext();

        color = context.getResources().getColor(R.color.colorPrimary);

        this.toolbar = toolbar;
        this.mPagerSlidingTabStrip = pagerSlidingTabStrip;
        this.headerBackground = headerBackground;
        this.statusBackground = statusBackground;
        this.mLogo = logo_white;

        finalScale = 0.6f;
        //heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);
        elevation = dpToPx(4, context);

        scrollMax = 250f;

        mPagerSlidingTabStrip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                finalTabsY = dpToPx(-2, context);

                mPagerSlidingTabStrip.getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
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
    }

    private void dispatchScrollOffset(Object source, float yOffset) {
        for (Object scroll : scrollViewList) {
            if (scroll != source) {
                calledScrollList.add(scroll);

                if (scroll instanceof RecyclerView) {
                    RecyclerView.LayoutManager layoutManager = ((RecyclerView) scroll).getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        linearLayoutManager.scrollToPositionWithOffset(0, (int) -yOffset);
                    }
                } else if (scroll instanceof WebView) {
                    ((WebView) scroll).scrollTo(0, (int) yOffset);
                }

                yOffsets.put(scroll, (int) yOffset);

                calledScrollList.remove(scroll);
            }
        }
    }

    public void onMaterialScrolled(Object source, float yOffset) {

        float scrollTop = -yOffset;

        { //parallax scroll of ImageView
            headerBackground.setTranslationY(scrollTop / 1.5f);
        }

        yOffset = minMax(0, yOffset, scrollMax);
        //Log.d("yOffset", "" + yOffset);

        dispatchScrollOffset(source, yOffset);

        float percent = yOffset / scrollMax;

        percent = minMax(0, percent, 1);
        {

            {
                // change color of
                // toolbar & viewpager indicator &  statusBaground

                setBackgroundColor(
                        colorWithAlpha(color, percent),

                        toolbar,
                        mPagerSlidingTabStrip,
                        statusBackground
                );

                setElevation(
                        (percent == 1) ? elevation : 0,

                        toolbar,
                        mPagerSlidingTabStrip,
                        mLogo
                );
            }


            { //move the viewpager indicator
                float newY = mPagerSlidingTabStrip.getY() + scrollTop;
                if (newY >= finalTabsY)
                    mPagerSlidingTabStrip.setTranslationY(scrollTop);
                else{
                }
            }

            { //move the header logo to toolbar
                mLogo.setTranslationY((finalTitleY - originalTitleY) * percent);
                mLogo.setTranslationX((finalTitleX - originalTitleX) * percent);

                float scale = (1 - percent) * (1 - finalScale) + finalScale;

                setScale(scale, mLogo);
            }
        }
    }

    private static void setElevation(float elevation, View... views) {
        for (View view : views) {
            ViewCompat.setElevation(view, elevation);
        }
    }

    private static void setBackgroundColor(int color, View... views) {
        for (View view : views) {
            view.setBackgroundColor(color);
        }
    }

    private static void setScale(float scale, View... views) {
        for (View view : views) {
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }

    private static float minMax(float min, float value, float max) {
        value = Math.min(value, max);
        value = Math.max(min, value);
        return value;
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
                    if (onScrollListener != null)
                        onScrollListener.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (onScrollListener != null)
                        onScrollListener.onScrolled(recyclerView, dx, dy);

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

    public void registerWebView(final ObservableWebView webView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (webView != null) {
            scrollViewList.add(webView);
            webView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);
                    if (calledScrollList.contains(webView)) {
                        calledScrollList.remove(webView);
                        return;
                    }
                    onMaterialScrolled(webView, i);
                }

                @Override
                public void onDownMotionEvent() {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onDownMotionEvent();
                }

                @Override
                public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onUpOrCancelMotionEvent(scrollState);
                }
            });
        }
    }
}
