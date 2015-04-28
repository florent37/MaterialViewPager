package com.github.florent37.materialviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
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

import static com.github.florent37.materialviewpager.Utils.pxToDp;

/**
 * Created by florentchampigny on 28/04/15.
 */
public class MaterialViewPager extends FrameLayout {

    private ViewGroup headerBackgroundContainer;
    private int headerLayoutId;

    private ViewGroup logoContainer;
    private int logoLayoutId;
    private int logoMarginTop;

    protected MaterialViewPagerHeader materialViewPagerHeader;

    protected Toolbar mToolbar;
    protected ViewPager mViewPager;
    protected PagerSlidingTabStrip mPagerTitleStrip;

    protected int headerHeight;
    protected int color;
    protected boolean hideToolbarAndTitle;
    protected boolean hideLogoWithFade;

    private void handleAttributes(Context context, AttributeSet attrs){
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaterialViewPager);
            {
                headerLayoutId = styledAttrs.getResourceId(R.styleable.MaterialViewPager_viewpager_header, -1);
                if(headerLayoutId == -1)
                    headerLayoutId = R.layout.material_view_pager_default_header;
            }
            {
                logoLayoutId = styledAttrs.getResourceId(R.styleable.MaterialViewPager_viewpager_logo, -1);
                logoMarginTop = styledAttrs.getDimensionPixelSize(R.styleable.MaterialViewPager_viewpager_logoMarginTop, 0);
            }
            {
                color = styledAttrs.getColor(R.styleable.MaterialViewPager_viewpager_color, 0);
            }
            {
                headerHeight = styledAttrs.getDimensionPixelOffset(R.styleable.MaterialViewPager_viewpager_headerHeight, 200);
                headerHeight = Math.round(pxToDp(headerHeight,context));
            }
            {
                hideToolbarAndTitle = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_hideToolbarAndTitle, false);
                hideLogoWithFade = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_hideLogoWithFade, false);
            }
            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MaterialViewPager(Context context) {
        super(context);
    }

    public MaterialViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context,attrs);
    }

    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context,attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        handleAttributes(context,attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(LayoutInflater.from(getContext()).inflate(R.layout.material_view_pager_layout,this,false));

        headerBackgroundContainer = (ViewGroup) findViewById(R.id.headerBackgroundContainer);
        logoContainer = (ViewGroup) findViewById(R.id.logoContainer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerTitleStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerTitleStrip);

        if(headerLayoutId != -1){
            headerBackgroundContainer.addView(LayoutInflater.from(getContext()).inflate(headerLayoutId,headerBackgroundContainer,false));
        }
        if(logoLayoutId != -1){
            logoContainer.addView(LayoutInflater.from(getContext()).inflate(logoLayoutId,logoContainer,false));
            if(logoMarginTop != 0){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) logoContainer.getLayoutParams();
                layoutParams.setMargins(0,logoMarginTop,0,0);
                logoContainer.setLayoutParams(layoutParams);
            }
        }

        if(!isInEditMode()) {
            materialViewPagerHeader = MaterialViewPagerHeader
                    .withToolbar(mToolbar)
                    .withToolbarLayoutBackground(findViewById(R.id.toolbar_layout_background))
                    .withPagerSlidingTabStrip(mPagerTitleStrip)
                    .withHeaderBackground(findViewById(R.id.headerBackground))
                    .withStatusBackground(findViewById(R.id.statusBackground))
                    .withLogo(logoContainer);

            MaterialViewPagerHelper.register((android.app.Activity) getContext(), new MaterialViewPagerAnimator(this));
        }

    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public PagerSlidingTabStrip getPagerTitleStrip() {
        return mPagerTitleStrip;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

}
