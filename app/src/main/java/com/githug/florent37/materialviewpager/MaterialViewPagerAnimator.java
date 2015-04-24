package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import static com.githug.florent37.materialviewpager.Utils.dpToPx;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class MaterialViewPagerAnimator {

    private Toolbar toolbar;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;

    private RecyclerView mRecyclerView;

    private View headerBackground;
    private ImageView headerBackgroundImage;
    private View toolbarBackground;
    private View statusBackground;
    private View mLogo;

    private Context context;

    float finalTitleY;

    float finalTabsY;
    float originalTabsY;

    float finalTitleX;
    float originalTitleY;
    float originalTitleX;
    float finalScale;
    float heightMaxScrollToolbar;
    float elevation;


    public MaterialViewPagerAnimator(Toolbar toolbar, PagerSlidingTabStrip pagerSlidingTabStrip, View headerBackground, View toolbarBackground, View statusBackground, View logo_white) {
        this.context = toolbar.getContext();

        this.toolbar = toolbar;
        this.mPagerSlidingTabStrip = pagerSlidingTabStrip;
        this.headerBackground = headerBackground;
        this.toolbarBackground = toolbarBackground;
        this.statusBackground = statusBackground;
        this.mLogo = logo_white;

        toolbarBackground.setAlpha(0);
        statusBackground.setAlpha(0);

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

        finalTabsY = dpToPx(10, context);
        originalTabsY = mPagerSlidingTabStrip.getTop();

        finalScale = 0.6f;

        heightMaxScrollToolbar = dpToPx(250f, context);

        elevation = dpToPx(4, context);
    }

    public void onMaterialScrolled(int yOffset) {

        {
            float newY = headerBackground.getY() + (-yOffset / 1.5f);
            if (newY <= 0)
                headerBackground.setTranslationY(-yOffset / 1.5f);
        }

        float percent = yOffset / heightMaxScrollToolbar;
        percent = Math.min(percent, 1);
        {
            toolbarBackground.setAlpha(percent);
            statusBackground.setAlpha(percent);

            if (percent == 1) {
                ViewCompat.setElevation(toolbarBackground, elevation);
                ViewCompat.setElevation(toolbar, elevation);
            } else {
                ViewCompat.setElevation(toolbarBackground, 0);
                ViewCompat.setElevation(toolbar, 0);
            }

            {
                float newY = mPagerSlidingTabStrip.getY()-yOffset;
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

    private List<RecyclerView> recyclerViewList = new ArrayList<>();
    private List<RecyclerView> calledRecyclerViewList = new ArrayList<>();
    private int totalScrolled = 0;

    public void registerRecyclerView(final RecyclerView recyclerView, final RecyclerView.OnScrollListener onScrollListener) {
        if (recyclerView != null) {
            recyclerViewList.add(recyclerView);
            recyclerView.scrollBy(0, totalScrolled);
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (calledRecyclerViewList.contains(recyclerView)) {
                        calledRecyclerViewList.remove(recyclerView);
                        return;
                    }

                    totalScrolled += dy;

                    onMaterialScrolled(totalScrolled);

                    for (RecyclerView r : recyclerViewList) {
                        if (r != recyclerView) {
                            calledRecyclerViewList.add(r);
                            r.scrollBy(0, dy);
                        }
                    }

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
}
