package com.github.florent37.materialviewpager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.florent37.materialviewpager.Utils.colorWithAlpha;
import static com.github.florent37.materialviewpager.Utils.dpToPx;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class MaterialViewPagerAnimator {

    private static final String TAG = MaterialViewPagerAnimator.class.getSimpleName();
    private Context context;
    private MaterialViewPagerHeader mHeader;

    private static final int ENTER_TOOLBAR_ANIMATION_DURATION = 600;

    private MaterialViewPager materialViewPager;

    public final float elevation;
    public final float scrollMax;
    public final float scrollMaxDp;

    private float lastYOffset = -1;
    private float lastPercent = 0;

    private MaterialViewPagerSettings settings;

    public MaterialViewPagerAnimator(MaterialViewPager materialViewPager) {

        this.settings = materialViewPager.settings;

        this.materialViewPager = materialViewPager;
        this.mHeader = materialViewPager.materialViewPagerHeader;
        this.context = mHeader.getContext();

        this.scrollMax = settings.headerHeight; // + 50;
        this.scrollMaxDp = Utils.dpToPx(this.scrollMax, context);

        if (this.mHeader.headerBackground != null) {
            this.mHeader.headerBackground.setBackgroundColor(this.settings.color);

            ViewGroup.LayoutParams layoutParams = this.mHeader.headerBackground.getLayoutParams();
            layoutParams.height = (int) Utils.dpToPx(this.settings.headerHeight + 60, context);
            this.mHeader.headerBackground.setLayoutParams(layoutParams);
        }
        if (this.mHeader.mPagerSlidingTabStrip != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mHeader.mPagerSlidingTabStrip.getLayoutParams();
            int marginTop = (int) Utils.dpToPx(this.settings.headerHeight - 40, context);
            layoutParams.setMargins(0, marginTop, 0, 0);
            this.mHeader.mPagerSlidingTabStrip.setLayoutParams(layoutParams);
        }
        if (this.mHeader.toolbarLayoutBackground != null) {
            ViewGroup.LayoutParams layoutParams = this.mHeader.toolbarLayoutBackground.getLayoutParams();
            layoutParams.height = (int) Utils.dpToPx(this.settings.headerHeight, context);
            this.mHeader.toolbarLayoutBackground.setLayoutParams(layoutParams);
        }

        mHeader.finalScale = 0.6f;
        //heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);
        elevation = dpToPx(4, context);
    }

    private void dispatchScrollOffset(Object source, float yOffset) {
        if (scrollViewList != null) {
            for (Object scroll : scrollViewList) {
                if (scroll != null && scroll != source) {
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
    }

    public void onMaterialScrolled(Object source, float yOffset) {

        if (yOffset == lastYOffset)
            return;

        float scrollTop = -yOffset;

        { //parallax scroll of ImageView
            if (mHeader.headerBackground != null)
                mHeader.headerBackground.setTranslationY(scrollTop / 1.5f);
        }

        //yOffset = ;
        Log.d("yOffset", "" + yOffset);

        dispatchScrollOffset(source, minMax(0, yOffset, scrollMaxDp));

        float percent = yOffset / scrollMax;

        percent = minMax(0, percent, 1);
        {

            {
                // change color of
                // toolbar & viewpager indicator &  statusBaground
                setColorPercent(percent);
                lastPercent = percent;

            }

            if (mHeader.mPagerSlidingTabStrip != null) { //move the viewpager indicator
                float newY = mHeader.mPagerSlidingTabStrip.getY() + scrollTop;
                if (newY >= mHeader.finalTabsY) {
                    mHeader.mPagerSlidingTabStrip.setTranslationY(scrollTop);
                    mHeader.toolbarLayoutBackground.setTranslationY(scrollTop);
                }
            }

            if (mHeader.mLogo != null) { //move the header logo to toolbar

                if (settings.hideLogoWithFade) {
                    mHeader.mLogo.setAlpha(1 - percent);
                    mHeader.mLogo.setTranslationY((mHeader.finalTitleY - mHeader.originalTitleY) * percent);
                } else {
                    mHeader.mLogo.setTranslationY((mHeader.finalTitleY - mHeader.originalTitleY) * percent);
                    mHeader.mLogo.setTranslationX((mHeader.finalTitleX - mHeader.originalTitleX) * percent);

                    float scale = (1 - percent) * (1 - mHeader.finalScale) + mHeader.finalScale;

                    setScale(scale, mHeader.mLogo);
                }
            }

            if (settings.hideToolbarAndTitle && mHeader.toolbarLayout != null) {
                boolean scrollUp = lastYOffset < yOffset;

                if (scrollUp) {
                    //Log.d(TAG, "scrollUp");
                    followScrollToolbarLayout(yOffset);
                } else {
                    //Log.d(TAG, "scrollDown");
                    if (yOffset > mHeader.toolbarLayout.getHeight()) {
                        animateEnterToolbarLayout(yOffset);
                    } else if (yOffset <= mHeader.toolbarLayout.getHeight()) {
                        if (headerAnimator != null) {
                            mHeader.toolbarLayout.setTranslationY(0);
                        } else {
                            headerYOffset = Float.MAX_VALUE;
                            followScrollToolbarLayout(yOffset);
                        }
                    }
                }
            }
        }

        if (headerAnimator != null && percent < 1) {
            headerAnimator.cancel();
            headerAnimator = null;
        }

        lastYOffset = yOffset;
    }

    public void setColor(int color) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mHeader.headerBackground, "backgroundColor", new int[]{settings.color, color});
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int colorAlpha = colorWithAlpha((Integer) animation.getAnimatedValue(), lastPercent);
                mHeader.statusBackground.setBackgroundColor(colorAlpha);
                mHeader.toolbar.setBackgroundColor(colorAlpha);
                mHeader.toolbarLayoutBackground.setBackgroundColor(colorAlpha);
                mHeader.mPagerSlidingTabStrip.setBackgroundColor(colorAlpha);
            }
        });
        colorAnim.start();
        settings.color = color;
    }

    public void setColorPercent(float percent) {
        // change color of
        // toolbar & viewpager indicator &  statusBaground

        setBackgroundColor(
                colorWithAlpha(settings.color, percent),
                mHeader.statusBackground
        );

        if (percent >= 1) {
            setBackgroundColor(
                    colorWithAlpha(settings.color, percent),
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip
            );
        } else {
            setBackgroundColor(
                    colorWithAlpha(settings.color, 0),
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip
            );
        }

        if (settings.enableToolbarElevation)
            setElevation(
                    (percent == 1) ? elevation : 0,
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip,
                    mHeader.mLogo
            );
    }

    private void followScrollToolbarLayout(float yOffset) {
        if (headerYOffset == Float.MAX_VALUE)
            headerYOffset = scrollMax;

        float diffOffsetScrollMax = headerYOffset - yOffset;
        if (diffOffsetScrollMax <= 0) {
            mHeader.toolbarLayout.setTranslationY(diffOffsetScrollMax);
        }
    }

    private void animateEnterToolbarLayout(float yOffset) {
        if (headerAnimator == null) {
            headerAnimator = ObjectAnimator.ofFloat(mHeader.toolbarLayout, "translationY", 0).setDuration(ENTER_TOOLBAR_ANIMATION_DURATION);
            headerAnimator.start();
            headerYOffset = yOffset;
        }
    }

    private float headerYOffset = Float.MAX_VALUE;
    private ObjectAnimator headerAnimator;

    private static void setElevation(float elevation, View... views) {
        for (View view : views) {
            if (view != null)
                ViewCompat.setElevation(view, elevation);
        }
    }

    private static void setBackgroundColor(int color, View... views) {
        for (View view : views) {
            if (view != null)
                view.setBackgroundColor(color);
        }
    }

    private static void setScale(float scale, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
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

    public void registerScrollView(final ObservableScrollView scrollView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (scrollView != null) {
            scrollViewList.add(scrollView);
            scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);
                    if (calledScrollList.contains(scrollView)) {
                        calledScrollList.remove(scrollView);
                        return;
                    }
                    onMaterialScrolled(scrollView, i);
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

    public int getHeaderHeight() {
        return settings.headerHeight;
    }

}
