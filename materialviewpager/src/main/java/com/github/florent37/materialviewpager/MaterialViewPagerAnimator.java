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
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
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
 * <p/>
 * Listen to Scrollable inside MaterialViewPager
 * When notified scroll, dispatch the current scroll to other scrollable
 * <p/>
 * Note : didn't want to translate the MaterialViewPager or intercept Scroll,
 * so added a ViewPager with scrollables containing a transparent placeholder on top
 * <p/>
 * When scroll, animate the MaterialViewPager Header (toolbar, logo, color ...)
 */
public class MaterialViewPagerAnimator {

    private static final String TAG = MaterialViewPagerAnimator.class.getSimpleName();
    private Context context;

    //contains MaterialViewPager subviews references
    private MaterialViewPagerHeader mHeader;

    //duration of translate header enter animation
    private static final int ENTER_TOOLBAR_ANIMATION_DURATION = 600;

    //reference to the current MaterialViewPager
    private MaterialViewPager materialViewPager;

    //final toolbar layout elevation (if attr viewpager_enableToolbarElevation = true)
    public final float elevation;

    //max scroll which will be dispatched for all scrollable
    public final float scrollMax;

    // equals scrollMax in DP (saved to avoir convert to dp anytime I use it)
    public final float scrollMaxDp;

    private float lastYOffset = -1; //the current yOffset
    private float lastPercent = 0; //the current Percent

    //contains the attributes given to MaterialViewPager from layout
    private MaterialViewPagerSettings settings;

    //list of all registered scrollers
    private List<Object> scrollViewList = new ArrayList<>();

    //temporary list of all called scrollers from dispatchScrollOffset
    private List<Object> calledScrollList = new ArrayList<>();

    //save all yOffsets of scrollables
    private HashMap<Object, Integer> yOffsets = new HashMap<>();

    public MaterialViewPagerAnimator(MaterialViewPager materialViewPager) {

        this.settings = materialViewPager.settings;

        this.materialViewPager = materialViewPager;
        this.mHeader = materialViewPager.materialViewPagerHeader;
        this.context = mHeader.getContext();

        // initialise the scrollMax to headerHeight, so until the first cell touch the top of the screen
        this.scrollMax = settings.headerHeight;
        //save in into dp once
        this.scrollMaxDp = Utils.dpToPx(this.scrollMax, context);

        //heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);
        elevation = dpToPx(4, context);
    }

    /**
     * When notified for scroll, dispatch it to all registered scrollables
     *
     * @param source
     * @param yOffset
     */
    private void dispatchScrollOffset(Object source, float yOffset) {
        if (scrollViewList != null) {
            for (Object scroll : scrollViewList) {

                //do not re-scroll the source
                if (scroll != null && scroll != source) {

                    //add it to calledScrollList so will not be notified again on the scroll's OnScrollListeners
                    calledScrollList.add(scroll);

                    if (scroll instanceof RecyclerView) {
                        //RecyclerView.scrollTo : UnsupportedOperationException
                        //Moved to the RecyclerView.LayoutManager.scrollToPositionWithOffset
                        //Have to be instanceOf RecyclerView.LayoutManager to work (so work with RecyclerView.GridLayoutManager)
                        RecyclerView.LayoutManager layoutManager = ((RecyclerView) scroll).getLayoutManager();
                        if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                            linearLayoutManager.scrollToPositionWithOffset(0, (int) -yOffset);
                        }
                    } else if (scroll instanceof ScrollView) {
                        ((ScrollView) scroll).scrollTo(0, (int) yOffset);
                    } else if (scroll instanceof ListView) {
                        ((ListView) scroll).scrollTo(0, (int) yOffset);
                    } else if (scroll instanceof WebView) {
                        ((WebView) scroll).scrollTo(0, (int) yOffset);
                    }

                    //save the current yOffset of the scrollable on the yOffsets hashmap
                    yOffsets.put(scroll, (int) yOffset);

                    //remove from calledScrollList to be notified for the next true scroll (from the user, not for dispatch)
                    calledScrollList.remove(scroll);
                }
            }
        }
    }

    /**
     * Called when a scroller(RecyclerView/ListView,ScrollView,WebView) scrolled by the user
     *
     * @param source  the scroller
     * @param yOffset the scroller current yOffset
     */
    public void onMaterialScrolled(Object source, float yOffset) {

        //only if yOffset changed
        if (yOffset == lastYOffset)
            return;

        float scrollTop = -yOffset;

        {
            //parallax scroll of the Background ImageView (the KenBurnsView)
            if (mHeader.headerBackground != null)
                mHeader.headerBackground.setTranslationY(scrollTop / 1.5f);
        }

        //Log.d("yOffset", "" + yOffset);

        //dispatch the new offset to all registered scrollables
        dispatchScrollOffset(source, minMax(0, yOffset, scrollMaxDp));

        float percent = yOffset / scrollMax;

        percent = minMax(0, percent, 1);
        {

            {
                // change color of toolbar & viewpager indicator &  statusBaground
                setColorPercent(percent);
                lastPercent = percent; //save the percent
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

    public void setColor(int color, int duration) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mHeader.headerBackground, "backgroundColor", new int[]{settings.color, color});
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(duration);
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


    public int getHeaderHeight() {
        return settings.headerHeight;
    }

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

    @Deprecated
    public void registerListView(final ObservableListView listView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (listView != null) {
            scrollViewList.add(listView);
            listView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);
                    if (calledScrollList.contains(listView)) {
                        calledScrollList.remove(listView);
                        return;
                    }
                    onMaterialScrolled(listView, i);
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
