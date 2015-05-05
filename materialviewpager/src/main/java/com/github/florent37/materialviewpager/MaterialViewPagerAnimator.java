package com.github.florent37.materialviewpager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import static com.github.florent37.materialviewpager.Utils.minMax;
import static com.github.florent37.materialviewpager.Utils.setBackgroundColor;
import static com.github.florent37.materialviewpager.Utils.setElevation;
import static com.github.florent37.materialviewpager.Utils.setScale;

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

    private static final Boolean ENABLE_LOG = true;

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

    //the last headerYOffset during scroll
    private float headerYOffset = Float.MAX_VALUE;

    //the tmp headerAnimator (not null if animating, else null)
    private ObjectAnimator headerAnimator;

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

        if(ENABLE_LOG)
            Log.d("yOffset", "" + yOffset);

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
                    if(ENABLE_LOG)
                        Log.d(TAG, "scrollUp");
                    followScrollToolbarLayout(yOffset);
                } else {
                    if(ENABLE_LOG)
                        Log.d(TAG, "scrollDown");
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

    /**
     * Change the color of the statusbackground, toolbar, toolbarlayout and pagertitlestrip
     * With a color transition animation
     * @param color the final color
     * @param duration the transition color animation duration
     */
    public void setColor(int color, int duration) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mHeader.headerBackground, "backgroundColor", settings.color, color);
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

        //set the new color as MaterialViewPager's color
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

    /**
     * move the toolbarlayout (containing toolbar & tabs)
     * following the current scroll
     */
    private void followScrollToolbarLayout(float yOffset) {
        if (headerYOffset == Float.MAX_VALUE)
            headerYOffset = scrollMax;

        float diffOffsetScrollMax = headerYOffset - yOffset;
        if (diffOffsetScrollMax <= 0) {
            mHeader.toolbarLayout.setTranslationY(diffOffsetScrollMax);
        }
    }

    /**
     * Animate enter toolbarlayout
     * @param yOffset
     */
    private void animateEnterToolbarLayout(float yOffset) {
        if (headerAnimator == null) {
            headerAnimator = ObjectAnimator.ofFloat(mHeader.toolbarLayout, "translationY", 0).setDuration(ENTER_TOOLBAR_ANIMATION_DURATION);
            headerAnimator.start();
            headerYOffset = yOffset;
        }
    }

    public int getHeaderHeight() {
        return settings.headerHeight;
    }

    //region register scrollables

    /**
     * Register a RecyclerView to the current MaterialViewPagerAnimator
     * Listen to RecyclerView.OnScrollListener so give to $[onScrollListener] your RecyclerView.OnScrollListener if you already use one
     * For loadmore or anything else
     * @param recyclerView the scrollable
     * @param onScrollListener use it if you want to get a callback of the RecyclerView
     */
    public void registerRecyclerView(final RecyclerView recyclerView, final RecyclerView.OnScrollListener onScrollListener) {
        if (recyclerView != null) {
            scrollViewList.add(recyclerView); //add to the scrollable list
            yOffsets.put(recyclerView, 0); //save the initial recyclerview's yOffset (0) into hashmap
            //only necessary for recyclerview

            //listen to scroll
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

                    //if scrolled from dispatch, remove & return -> so skip
                    if (calledScrollList.contains(recyclerView)) {
                        calledScrollList.remove(recyclerView);
                        return;
                    }

                    scrollY += dy;
                    yOffsets.put(recyclerView, scrollY); //save the new offset

                    onMaterialScrolled(recyclerView, scrollY);
                }
            });
        }
    }

    /**
     * Register a ScrollView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     * @param scrollView the scrollable
     * @param observableScrollViewCallbacks use it if you want to get a callback of the RecyclerView
     */
    public void registerScrollView(final ObservableScrollView scrollView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (scrollView != null) {
            scrollViewList.add(scrollView);  //add to the scrollable list
            scrollView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);

                    //if scrolled from dispatch, remove & return -> so skip
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

    /**
     * Register a WebView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     * @param webView the scrollable
     * @param observableScrollViewCallbacks use it if you want to get a callback of the RecyclerView
     */
    public void registerWebView(final ObservableWebView webView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (webView != null) {
            scrollViewList.add(webView);  //add to the scrollable list
            webView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);

                    //if scrolled from dispatch, remove & return -> so skip
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

    /**
     * Register a ListView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     * @param listView the scrollable
     * @param observableScrollViewCallbacks use it if you want to get a callback of the RecyclerView
     */
    @Deprecated
    public void registerListView(final ObservableListView listView, final ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (listView != null) {
            scrollViewList.add(listView);  //add to the scrollable list
            listView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
                @Override
                public void onScrollChanged(int i, boolean b, boolean b2) {
                    if (observableScrollViewCallbacks != null)
                        observableScrollViewCallbacks.onScrollChanged(i, b, b2);

                    //if scrolled from dispatch, remove & return -> so skip
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

    //endregion
}
