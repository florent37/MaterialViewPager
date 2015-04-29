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

    private ViewGroup logoContainer;

    protected MaterialViewPagerHeader materialViewPagerHeader;

    protected Toolbar mToolbar;
    protected ViewPager mViewPager;
    protected PagerSlidingTabStrip mPagerTitleStrip;

    protected MaterialViewPagerSettings settings = new MaterialViewPagerSettings();

    public MaterialViewPager(Context context) {
        super(context);
    }

    public MaterialViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        settings.handleAttributes(context,attrs);
    }

    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        settings.handleAttributes(context,attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        settings.handleAttributes(context,attrs);
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

        if(settings.headerLayoutId != -1){
            headerBackgroundContainer.addView(LayoutInflater.from(getContext()).inflate(settings.headerLayoutId,headerBackgroundContainer,false));
        }
        if(settings.logoLayoutId != -1){
            logoContainer.addView(LayoutInflater.from(getContext()).inflate(settings.logoLayoutId,logoContainer,false));
            if(settings.logoMarginTop != 0){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) logoContainer.getLayoutParams();
                layoutParams.setMargins(0,settings.logoMarginTop,0,0);
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
