package com.github.florent37.materialviewpager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.github.florent37.materialviewpager.Utils.canScroll;
import static com.github.florent37.materialviewpager.Utils.colorWithAlpha;
import static com.github.florent37.materialviewpager.Utils.dpToPx;
import static com.github.florent37.materialviewpager.Utils.getTheVisibileView;
import static com.github.florent37.materialviewpager.Utils.minMax;
import static com.github.florent37.materialviewpager.Utils.scrollTo;
import static com.github.florent37.materialviewpager.Utils.setBackgroundColor;
import static com.github.florent37.materialviewpager.Utils.setElevation;
import static com.github.florent37.materialviewpager.Utils.setScale;

/**
 * Created by florentchampigny on 24/04/15.
 * <p>
 * Listen to Scrollable inside MaterialViewPager
 * When notified scroll, dispatch the current scroll to other scrollable
 * <p>
 * Note : didn't want to translate the MaterialViewPager or intercept Scroll,
 * so added a ViewPager with scrollables containing a transparent placeholder on top
 * <p>
 * When scroll, animate the MaterialViewPager Header (toolbar, logo, color ...)
 */
public class MaterialViewPagerAnimator {

    private static final String TAG = "MaterialViewPager";

    //duration of translate header enter animation
    private static final int ENTER_TOOLBAR_ANIMATION_DURATION = 300;
    public static Boolean ENABLE_LOG = false;
    //final toolbar layout elevation (if attr viewpager_enableToolbarElevation = true)
    public final float elevation;
    //max scroll which will be dispatched for all scrollable
    private final float scrollMax;
    // equals scrollMax in DP (saved to avoir convert to dp anytime I use it)
    private final float scrollMaxDp;
    float lastYOffset = -1; //the current yOffset
    private float lastPercent = 0; //the current Percent
    //contains the attributes given to MaterialViewPager from layout
    private MaterialViewPagerSettings settings;
    //list of all registered scrollers
    private List<View> scrollViewList = new ArrayList<>();
    //save all yOffsets of scrollables
    private HashMap<Object, Integer> yOffsets = new HashMap<>();
    private boolean followScrollToolbarIsVisible = false;
    private float firstScrollValue = Float.MIN_VALUE;
    private boolean justToolbarAnimated = false;
    //intial distance between pager & toolbat
    private float initialDistance = -1;
    //contains MaterialViewPager subviews references
    private MaterialViewPagerHeader mHeader;
    //the tmp headerAnimator (not null if animating, else null)
    private ValueAnimator headerAnimator;

    MaterialViewPagerAnimator(MaterialViewPager materialViewPager) {

        this.settings = materialViewPager.settings;

        this.mHeader = materialViewPager.materialViewPagerHeader;
        Context context = mHeader.getContext();

        // initialise the scrollMax to headerHeight, so until the first cell touch the top of the screen
        this.scrollMax = this.settings.headerHeight;
        //save in into dp once
        this.scrollMaxDp = Utils.dpToPx(this.scrollMax, context);

        //heightMaxScrollToolbar = context.getResources().getDimension(R.dimen.material_viewpager_padding_top);
        elevation = dpToPx(4, context);
    }

    /**
     * Called when a scroller(RecyclerView/ListView,ScrollView,WebView) scrolled by the user
     *
     * @param source  the scroller
     * @param yOffset the scroller current yOffset
     */
    private boolean onMaterialScrolled(Object source, float yOffset) {

        if (initialDistance == -1 || initialDistance == 0) {
            initialDistance = mHeader.mPagerSlidingTabStrip.getTop() - mHeader.toolbar.getBottom();
        }

        //only if yOffset changed
        if (yOffset == lastYOffset) {
            return false;
        }

        float scrollTop = -yOffset;

        {
            //parallax scroll of the Background ImageView (the KenBurnsView)
            if (mHeader.headerBackground != null) {

                if (this.settings.parallaxHeaderFactor != 0) {
                    ViewCompat.setTranslationY(mHeader.headerBackground, scrollTop / this.settings.parallaxHeaderFactor);
                }

                if (ViewCompat.getY(mHeader.headerBackground) >= 0) {
                    ViewCompat.setY(mHeader.headerBackground, 0);
                }
            }

        }

        log("yOffset" + yOffset);

        //dispatch the new offset to all registered scrollables
        dispatchScrollOffset(source, minMax(0, yOffset, scrollMaxDp));

        float percent = yOffset / scrollMax;

        log("percent1" + percent);

        if (percent != 0) {
            //distance between pager & toolbar
            float newDistance = ViewCompat.getY(mHeader.mPagerSlidingTabStrip) - mHeader.toolbar.getBottom();

            percent = 1 - newDistance / initialDistance;

            log("percent2" + percent);
        }

        if (Float.isNaN(percent)) //fix for orientation change
        {
            return false;
        }

        //fix quick scroll
        if (percent == 0 && headerAnimator != null) {
            cancelHeaderAnimator();
            ViewCompat.setTranslationY(mHeader.toolbarLayout, 0);
        }

        percent = minMax(0, percent, 1);
        {

            if (!settings.toolbarTransparent) {
                // change color of toolbar & viewpager indicator &  statusBaground
                setColorPercent(percent);
            } else {
                if (justToolbarAnimated) {
                    if (toolbarJoinsTabs()) {
                        setColorPercent(1);
                    } else if (lastPercent != percent) {
                        animateColorPercent(0, 200);
                    }
                }
            }

            lastPercent = percent; //save the percent

            if (mHeader.mPagerSlidingTabStrip != null) { //move the viewpager indicator
                //float newY = ViewCompat.getY(mHeader.mPagerSlidingTabStrip) + scrollTop;

                log("" + scrollTop);

                //mHeader.mPagerSlidingTabStrip.setTranslationY(mHeader.getToolbar().getBottom()-mHeader.mPagerSlidingTabStrip.getY());
                if (scrollTop <= 0) {
                    ViewCompat.setTranslationY(mHeader.mPagerSlidingTabStrip, scrollTop);
                    ViewCompat.setTranslationY(mHeader.toolbarLayoutBackground, scrollTop);

                    //when
                    if (ViewCompat.getY(mHeader.mPagerSlidingTabStrip) < mHeader.getToolbar().getBottom()) {
                        float ty = mHeader.getToolbar().getBottom() - mHeader.mPagerSlidingTabStrip.getTop();
                        ViewCompat.setTranslationY(mHeader.mPagerSlidingTabStrip, ty);
                        ViewCompat.setTranslationY(mHeader.toolbarLayoutBackground, ty);
                    }
                }

            }

            if (mHeader.mLogo != null) { //move the header logo to toolbar

                if (this.settings.hideLogoWithFade) {
                    ViewCompat.setAlpha(mHeader.mLogo, 1 - percent);
                    ViewCompat.setTranslationY(mHeader.mLogo, (mHeader.finalTitleY - mHeader.originalTitleY) * percent);
                } else {
                    ViewCompat.setTranslationY(mHeader.mLogo, (mHeader.finalTitleY - mHeader.originalTitleY) * percent);
                    ViewCompat.setTranslationX(mHeader.mLogo, (mHeader.finalTitleX - mHeader.originalTitleX) * percent);

                    float scale = (1 - percent) * (1 - mHeader.finalScale) + mHeader.finalScale;
                    setScale(scale, mHeader.mLogo);
                }
            }

            if (this.settings.hideToolbarAndTitle && mHeader.toolbarLayout != null) {
                boolean scrollUp = lastYOffset < yOffset;

                if (scrollUp) {
                    scrollUp(yOffset);
                } else {
                    scrollDown(yOffset);
                }
            }
        }

        if (headerAnimator != null && percent < 1) {
            cancelHeaderAnimator();
        }

        lastYOffset = yOffset;

        return true;
    }

    /**
     * Change the color of the statusbackground, toolbar, toolbarlayout and pagertitlestrip
     * With a color transition animation
     *
     * @param color    the final color
     * @param duration the transition color animation duration
     */
    void setColor(int color, int duration) {
        final ValueAnimator colorAnim = ObjectAnimator.ofInt(mHeader.headerBackground, "backgroundColor", settings.color, color);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(duration);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final int animatedValue = (Integer) animation.getAnimatedValue();
                int colorAlpha = colorWithAlpha(animatedValue, lastPercent);
                mHeader.headerBackground.setBackgroundColor(colorAlpha);
                mHeader.statusBackground.setBackgroundColor(colorAlpha);
                mHeader.toolbar.setBackgroundColor(colorAlpha);
                mHeader.toolbarLayoutBackground.setBackgroundColor(colorAlpha);
                mHeader.mPagerSlidingTabStrip.setBackgroundColor(colorAlpha);

                //set the new color as MaterialViewPager's color
                settings.color = animatedValue;
            }
        });
        colorAnim.start();
    }

    public void animateColorPercent(float percent, int duration) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(lastPercent, percent);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setColorPercent((float) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void setColorPercent(float percent) {
        // change color of
        // toolbar & viewpager indicator &  statusBaground

        setBackgroundColor(
                colorWithAlpha(this.settings.color, percent),
                mHeader.statusBackground
        );

        if (percent >= 1) {
            setBackgroundColor(
                    colorWithAlpha(this.settings.color, percent),
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip
            );
        } else {
            setBackgroundColor(
                    colorWithAlpha(this.settings.color, 0),
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip
            );
        }

        if (this.settings.enableToolbarElevation && toolbarJoinsTabs()) {
            setElevation(
                    (percent == 1) ? elevation : 0,
                    mHeader.toolbar,
                    mHeader.toolbarLayoutBackground,
                    mHeader.mPagerSlidingTabStrip,
                    mHeader.mLogo
            );
        }
    }

    public int getHeaderHeight() {
        return this.settings.headerHeight;
    }

    /**
     * Register a RecyclerView to the current MaterialViewPagerAnimator
     * Listen to RecyclerView.OnScrollListener so give to $[onScrollListener] your RecyclerView.OnScrollListener if you already use one
     * For loadmore or anything else
     *
     * @param recyclerView the scrollable
     */
    void registerRecyclerView(final RecyclerView recyclerView) {
        if (recyclerView != null && !scrollViewList.contains(recyclerView)) {
            scrollViewList.add(recyclerView); //add to the scrollable list
            yOffsets.put(recyclerView, recyclerView.getScrollY()); //save the initial recyclerview's yOffset (0) into hashmap
            //only necessary for recyclerview

            //listen to scroll
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                boolean firstZeroPassed;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int yOffset = yOffsets.get(recyclerView);
                    if(yOffset < 0) {
                        yOffset = 0;
                    }
                    yOffset += dy;
                    yOffsets.put(recyclerView, yOffset); //save the new offset

                    //first time you get 0, don't share it to others scrolls
                    if (yOffset == 0 && !firstZeroPassed) {
                        firstZeroPassed = true;
                        return;
                    }

                    //only if yOffset changed
                    if (isNewYOffset(yOffset)) {
                        onMaterialScrolled(recyclerView, yOffset);
                    }
                }
            });

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    setScrollOffset(recyclerView, lastYOffset);
                }
            });
        }
    }

    /**
     * Register a ScrollView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     *
     * @param scrollView the scrollable
     */
    void registerScrollView(final NestedScrollView scrollView) {
        if (scrollView != null) {
            scrollViewList.add(scrollView);  //add to the scrollable list

            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

                boolean firstZeroPassed;

                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //first time you get 0, don't share it to others scrolls
                    if (scrollY == 0 && !firstZeroPassed) {
                        firstZeroPassed = true;
                        return;
                    }

                    //only if yOffset changed
                    if (isNewYOffset(scrollY)) {
                        onMaterialScrolled(scrollView, scrollY);
                    }
                }
            });

            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    setScrollOffset(scrollView, lastYOffset);
                }
            });
        }
    }

    void restoreScroll(final float scroll, final MaterialViewPagerSettings settings) {
        //try to scroll up, on a looper to wait until restored
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!onMaterialScrolled(null, scroll)) {
                    restoreScroll(scroll, settings);
                }
            }
        }, 100);

    }

    void onViewPagerPageChanged() {
        scrollDown(lastYOffset);

        View visibleView = getTheVisibileView(scrollViewList);
        if (!canScroll(visibleView)) {
            followScrollToolbarLayout(0);
            onMaterialScrolled(visibleView, 0);
        }
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
                    setScrollOffset(scroll, yOffset);
                }
            }
        }
    }

    private boolean isNewYOffset(int yOffset) {
        if (lastYOffset == -1) {
            return true;
        } else {
            return yOffset != lastYOffset;
        }
    }

    /**
     * When notified for scroll, dispatch it to all registered scrollables
     *
     * @param scroll
     * @param yOffset
     */
    private void setScrollOffset(Object scroll, float yOffset) {
        //do not re-scroll the source
        if (scroll != null && yOffset >= 0) {

            scrollTo(scroll, yOffset);

            //save the current yOffset of the scrollable on the yOffsets hashmap
            yOffsets.put(scroll, (int) yOffset);
        }
    }

    private void cancelHeaderAnimator() {
        if (headerAnimator != null) {
            headerAnimator.cancel();
            headerAnimator = null;
        }
    }

    //region register scrollables

    private void scrollUp(float yOffset) {
        log("scrollUp");

        followScrollToolbarLayout(yOffset);
    }

    private void log(String scrollUp) {
        if (ENABLE_LOG) {
            Log.d(TAG, scrollUp);
        }
    }

    private void scrollDown(float yOffset) {
        log("scrollDown");
        if (yOffset > mHeader.toolbarLayout.getHeight() * 1.5f) {
            animateEnterToolbarLayout(yOffset);
        } else {
            if (headerAnimator != null) {
                followScrollToolbarIsVisible = true;
            } else {
                followScrollToolbarLayout(yOffset);
            }
        }
    }

    private boolean toolbarJoinsTabs() {
        return (mHeader.toolbar.getBottom() == mHeader.mPagerSlidingTabStrip.getTop() + ViewCompat.getTranslationY(mHeader.mPagerSlidingTabStrip));
    }

    //endregion

    /**
     * move the toolbarlayout (containing toolbar & tabs)
     * following the current scroll
     */
    private void followScrollToolbarLayout(float yOffset) {
        if (mHeader.toolbar.getBottom() == 0) {
            return;
        }

        if (toolbarJoinsTabs()) {
            if (firstScrollValue == Float.MIN_VALUE) {
                firstScrollValue = yOffset;
            }

            float translationY = firstScrollValue - yOffset;

            if (translationY > 0) {
                translationY = 0;
            }

            log("translationY " + translationY);

            ViewCompat.setTranslationY(mHeader.toolbarLayout, translationY);
        } else {
            ViewCompat.setTranslationY(mHeader.toolbarLayout, 0);
            justToolbarAnimated = false;
        }

        followScrollToolbarIsVisible = (ViewCompat.getY(mHeader.toolbarLayout) >= 0);
    }

    /**
     * Animate enter toolbarlayout
     *
     * @param yOffset
     */
    private void animateEnterToolbarLayout(float yOffset) {
        if (!followScrollToolbarIsVisible && headerAnimator != null) {
            headerAnimator.cancel();
            headerAnimator = null;
        }

        if (headerAnimator == null) {

            headerAnimator = ObjectAnimator.ofFloat(mHeader.toolbarLayout, View.TRANSLATION_Y, 0);
            headerAnimator.setDuration(ENTER_TOOLBAR_ANIMATION_DURATION);
            headerAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    super.onAnimationEnd(animation);
                    followScrollToolbarIsVisible = true;
                    firstScrollValue = Float.MIN_VALUE;
                    justToolbarAnimated = true;
                }
            });
            headerAnimator.start();
        }
    }
}
