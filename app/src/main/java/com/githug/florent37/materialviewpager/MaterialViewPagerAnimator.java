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

    private Context context;
    private MaterialViewPagerHeader mHeader;

    private int color;

    //float heightMaxScrollToolbar;
    float elevation;

    float scrollMax;


    public MaterialViewPagerAnimator(MaterialViewPagerHeader header) {
        this.mHeader = header;
        this.context = mHeader.getContext();

        color = context.getResources().getColor(R.color.colorPrimary);

        mHeader.finalScale = 0.6f;
        //heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);
        elevation = dpToPx(4, context);

        scrollMax = 250f;
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
            mHeader.headerBackground.setTranslationY(scrollTop / 1.5f);
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

                        mHeader.toolbar,
                        mHeader.mPagerSlidingTabStrip,
                        mHeader.statusBackground
                );

                setElevation(
                        (percent == 1) ? elevation : 0,

                        mHeader.toolbar,
                        mHeader.mPagerSlidingTabStrip,
                        mHeader.mLogo
                );
            }


            { //move the viewpager indicator
                float newY = mHeader.mPagerSlidingTabStrip.getY() + scrollTop;
                if (newY >= mHeader.finalTabsY)
                    mHeader.mPagerSlidingTabStrip.setTranslationY(scrollTop);
                else{
                }
            }

            { //move the header logo to toolbar
                mHeader.mLogo.setTranslationY((mHeader.finalTitleY - mHeader.originalTitleY) * percent);
                mHeader.mLogo.setTranslationX((mHeader.finalTitleX - mHeader.originalTitleX) * percent);

                float scale = (1 - percent) * (1 - mHeader.finalScale) + mHeader.finalScale;

                setScale(scale, mHeader.mLogo);
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
