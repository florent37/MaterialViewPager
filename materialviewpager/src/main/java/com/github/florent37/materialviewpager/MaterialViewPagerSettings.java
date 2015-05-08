package com.github.florent37.materialviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import static com.github.florent37.materialviewpager.Utils.pxToDp;

/**
 * Created by florentchampigny on 29/04/15.
 *
 * Save attributes given to MaterialViewPager from layout
 */
public class MaterialViewPagerSettings {

    //attributes are protected and can be used by class from the same package
    //com.github.florent37.materialviewpager

    protected int headerLayoutId;
    protected int pagerTitleStripId;

    protected int logoLayoutId;
    protected int logoMarginTop;

    protected int headerAdditionalHeight;

    protected int headerHeight;
    protected int headerHeightPx;
    protected int color;

    protected float headerAlpha;
    protected float parallaxHeaderFactor;

    protected boolean hideToolbarAndTitle;
    protected boolean hideLogoWithFade;
    protected boolean enableToolbarElevation;

    /**
     * Retrieve attributes from the MaterialViewPager
     * @param context
     * @param attrs
     */
    protected void handleAttributes(Context context, AttributeSet attrs){
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaterialViewPager);
            {
                headerLayoutId = styledAttrs.getResourceId(R.styleable.MaterialViewPager_viewpager_header, -1);
                if(headerLayoutId == -1)
                    headerLayoutId = R.layout.material_view_pager_default_header;
            }
            {
                pagerTitleStripId = styledAttrs.getResourceId(R.styleable.MaterialViewPager_viewpager_pagerTitleStrip, -1);
                if(pagerTitleStripId == -1)
                    pagerTitleStripId = R.layout.material_view_pager_pagertitlestrip_standard;
            }
            {
                logoLayoutId = styledAttrs.getResourceId(R.styleable.MaterialViewPager_viewpager_logo, -1);
                logoMarginTop = styledAttrs.getDimensionPixelSize(R.styleable.MaterialViewPager_viewpager_logoMarginTop, 0);
            }
            {
                color = styledAttrs.getColor(R.styleable.MaterialViewPager_viewpager_color, 0);
            }
            {
                headerHeightPx = styledAttrs.getDimensionPixelOffset(R.styleable.MaterialViewPager_viewpager_headerHeight, 200);
                headerHeight = Math.round(pxToDp(headerHeightPx, context)); //convert to dp
            }
            {
                headerAdditionalHeight = styledAttrs.getDimensionPixelOffset(R.styleable.MaterialViewPager_viewpager_headerAdditionalHeight, 60);
            }
            {
                headerAlpha = styledAttrs.getFloat(R.styleable.MaterialViewPager_viewpager_headerAlpha, 0.5f);
            }
            {
                parallaxHeaderFactor = styledAttrs.getFloat(R.styleable.MaterialViewPager_viewpager_parallaxHeaderFactor, 1.5f);
                parallaxHeaderFactor = Math.max(parallaxHeaderFactor,1); //min=1
            }
            {
                hideToolbarAndTitle = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_hideToolbarAndTitle, false);
                hideLogoWithFade = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_hideLogoWithFade, false);
            }
            {
                enableToolbarElevation = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_enableToolbarElevation, false);
            }
            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
