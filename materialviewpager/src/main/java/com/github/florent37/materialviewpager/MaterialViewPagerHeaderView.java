package com.github.florent37.materialviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by florentchampigny on 26/04/15.
 * A placeholder view used to add a transparent padding on top of a Scroller
 * RecyclerView : use RecyclerViewMaterialAdapter
 * ListView : use ListViewMaterialAdapter (smoother if use RecyclerView)
 * ScrollView : add a MaterialViewPagerHeaderView on top of your ScrollView (with LinearLayout vertical)
 */
public class MaterialViewPagerHeaderView extends View {
    public MaterialViewPagerHeaderView(Context context) {
        super(context);
    }

    public MaterialViewPagerHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialViewPagerHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialViewPagerHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setMaterialHeight() {
        //get the MaterialViewPagerAnimator attached to this activity
        //to retrieve the declared header height
        //and set it as current view height (+10dp margin)

        MaterialViewPagerAnimator animator = MaterialViewPagerHelper.getAnimator(getContext());
        if (animator != null) {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = Math.round(Utils.dpToPx(animator.getHeaderHeight() + 10, getContext()));
            super.setLayoutParams(params);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    setMaterialHeight();
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });
        }
    }
}
