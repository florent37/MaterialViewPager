package com.githug.florent37.materialviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by florentchampigny on 26/04/15.
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(!isInEditMode()){
            MaterialViewPagerAnimator animator = MaterialViewPager.getAnimator(getContext());
            if(animator != null) {
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = Math.round(Utils.dpToPx(animator.getHeaderHeight()+10,getContext()));
                setLayoutParams(params);
            }
        }
    }
}
