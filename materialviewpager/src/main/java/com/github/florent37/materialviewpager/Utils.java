package com.github.florent37.materialviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class Utils {

    public static boolean isLolipop(){
        return (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    /**
     * convert dp to px
     */
    public static float dpToPx(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * convert px to dp
     */
    public static float pxToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /*
     * Create a color from [$color].RGB and then add an alpha with 255*[$percent]
     */
    public static int colorWithAlpha(int color, float percent){
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int alpha = Math.round(percent*255);

        return Color.argb(alpha,r,g,b);
    }

    public static float minMax(float min, float value, float max) {
        value = Math.min(value, max);
        value = Math.max(min, value);
        return value;
    }


    /**
     * modify the scale of multiples views
     * @param scale the new scale
     * @param views
     */
    public static void setScale(float scale, View... views) {
        for (View view : views) {
            if (view != null) {
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {

                    view.setScaleX(scale);
                    view.setScaleY(scale);
                }else{
                    ViewHelper.setScaleX(view, scale);
                    ViewHelper.setScaleY(view, scale);
                }
            }
        }
    }

    /**
     * modify the elevation of multiples views
     * @param elevation the new elevation
     * @param views
     */
    public static void setElevation(float elevation, View... views) {
        for (View view : views) {
            if (view != null)
                ViewCompat.setElevation(view, elevation);
        }
    }

    /**
     * modify the backgroundcolor of multiples views
     * @param color the new backgroundcolor
     * @param views
     */
    public static void setBackgroundColor(int color, View... views) {
        for (View view : views) {
            if (view != null)
                view.setBackgroundColor(color);
        }
    }

    /**
     * modify the alpha of multiples views
     * @param view
     * @param alpha value
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setAlphaForView(View view, float alpha) {
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
            AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
            animation.setDuration(0);
            animation.setFillAfter(true);
            view.startAnimation(animation);
        }else{
            view.setAlpha(alpha);
        }
    }
}
