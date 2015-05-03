package com.github.florent37.materialviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStrip;

/**
 * Created by florentchampigny on 28/04/15.
 */
public class MaterialViewPager extends FrameLayout {

    private ViewGroup headerBackgroundContainer;
    private ViewGroup pagerTitleStripContainer;
    private ViewGroup logoContainer;

    protected MaterialViewPagerHeader materialViewPagerHeader;

    protected Toolbar mToolbar;
    protected ViewPager mViewPager;

    protected View headerBackground;
    protected View toolbarLayoutBackground;

    protected MaterialViewPagerSettings settings = new MaterialViewPagerSettings();

    public MaterialViewPager(Context context) {
        super(context);
    }

    public MaterialViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        settings.handleAttributes(context, attrs);
    }

    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        settings.handleAttributes(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        settings.handleAttributes(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(LayoutInflater.from(getContext()).inflate(R.layout.material_view_pager_layout, this, false));

        headerBackgroundContainer = (ViewGroup) findViewById(R.id.headerBackgroundContainer);
        pagerTitleStripContainer = (ViewGroup) findViewById(R.id.pagerTitleStripContainer);
        logoContainer = (ViewGroup) findViewById(R.id.logoContainer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        if (settings.headerLayoutId != -1) {
            headerBackgroundContainer.addView(LayoutInflater.from(getContext()).inflate(settings.headerLayoutId, headerBackgroundContainer, false));
        }

        if (isInEditMode()) { //preview titlestrip
            settings.pagerTitleStripId = R.layout.tools_material_view_pager_pagertitlestrip;
        }
        if (settings.pagerTitleStripId != -1) {
            pagerTitleStripContainer.addView(LayoutInflater.from(getContext()).inflate(settings.pagerTitleStripId, pagerTitleStripContainer, false));
        }

        if (settings.logoLayoutId != -1) {
            logoContainer.addView(LayoutInflater.from(getContext()).inflate(settings.logoLayoutId, logoContainer, false));
            if (settings.logoMarginTop != 0) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) logoContainer.getLayoutParams();
                layoutParams.setMargins(0, settings.logoMarginTop, 0, 0);
                logoContainer.setLayoutParams(layoutParams);
            }
        }

        headerBackground = findViewById(R.id.headerBackground);
        toolbarLayoutBackground = findViewById(R.id.toolbar_layout_background);

        initialiseHeights();

        if (!isInEditMode()) {
            materialViewPagerHeader = MaterialViewPagerHeader
                    .withToolbar(mToolbar)
                    .withToolbarLayoutBackground(toolbarLayoutBackground)
                    .withPagerSlidingTabStrip(pagerTitleStripContainer)
                    .withHeaderBackground(headerBackground)
                    .withStatusBackground(findViewById(R.id.statusBackground))
                    .withLogo(logoContainer);

            MaterialViewPagerHelper.register((android.app.Activity) getContext(), new MaterialViewPagerAnimator(this));
        }else{
            View sample = LayoutInflater.from(getContext()).inflate(R.layout.tools_list_items, pagerTitleStripContainer, false);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sample.getLayoutParams();
            int marginTop = Math.round(Utils.dpToPx(settings.headerHeight + 10, getContext()));
            params.setMargins(0,marginTop,0,0);
            super.setLayoutParams(params);

            addView(sample);
        }
    }

    private void initialiseHeights(){
        if (headerBackground != null) {
            headerBackground.setBackgroundColor(this.settings.color);

            ViewGroup.LayoutParams layoutParams = headerBackground.getLayoutParams();
            layoutParams.height = (int) Utils.dpToPx(this.settings.headerHeight + 60, getContext());
            headerBackground.setLayoutParams(layoutParams);
        }
        if (pagerTitleStripContainer != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pagerTitleStripContainer.getLayoutParams();
            int marginTop = (int) Utils.dpToPx(this.settings.headerHeight - 40, getContext());
            layoutParams.setMargins(0, marginTop, 0, 0);
            pagerTitleStripContainer.setLayoutParams(layoutParams);
        }
        if (toolbarLayoutBackground != null) {
            ViewGroup.LayoutParams layoutParams = toolbarLayoutBackground.getLayoutParams();
            layoutParams.height = (int) Utils.dpToPx(this.settings.headerHeight, getContext());
            toolbarLayoutBackground.setLayoutParams(layoutParams);
        }
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public PagerSlidingTabStrip getPagerTitleStrip() {
        return (PagerSlidingTabStrip) pagerTitleStripContainer.findViewById(R.id.materialviewpager_pagerTitleStrip);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setImageUrl(String imageUrl, int fadeDuration) {
        if (imageUrl != null) {
            final MaterialViewPagerImageHeader headerBackgroundImage = (MaterialViewPagerImageHeader) findViewById(R.id.materialviewpager_imageHeader);
            //if using MaterialViewPagerImageHeader
            if (headerBackgroundImage != null) {
                headerBackgroundImage.setAlpha(settings.headerAlpha);
                headerBackgroundImage.setImageUrl(imageUrl, fadeDuration);
            }
        }
    }

    public void setImageDrawable(Drawable drawable, int fadeDuration) {
        if (drawable != null) {
            final MaterialViewPagerImageHeader headerBackgroundImage = (MaterialViewPagerImageHeader) findViewById(R.id.materialviewpager_imageHeader);
            //if using MaterialViewPagerImageHeader
            if (headerBackgroundImage != null) {
                headerBackgroundImage.setAlpha(settings.headerAlpha);
                headerBackgroundImage.setImageDrawable(drawable, fadeDuration);
            }
        }
    }

    public void setColor(int color, int fadeDuration) {
        MaterialViewPagerHelper.getAnimator(getContext()).setColor(color, fadeDuration * 2);
    }

}
