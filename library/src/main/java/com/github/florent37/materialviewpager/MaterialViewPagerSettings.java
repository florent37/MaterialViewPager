package com.github.florent37.materialviewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import static com.github.florent37.materialviewpager.Utils.pxToDp;

/**
 * Created by florentchampigny on 29/04/15.
 */
public class MaterialViewPagerSettings {

    protected int headerLayoutId;

    protected int logoLayoutId;
    protected int logoMarginTop;

    protected int headerHeight;
    protected int color;
    protected boolean hideToolbarAndTitle;
    protected boolean hideLogoWithFade;
    protected boolean enableToolbarElevation;

    protected void handleAttributes(Context context, AttributeSet attrs){
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
            {
                enableToolbarElevation = styledAttrs.getBoolean(R.styleable.MaterialViewPager_viewpager_enableToolbarElevation, false);
            }
            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
