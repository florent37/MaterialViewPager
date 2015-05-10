package com.github.florent37.materialviewpager;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class Utils {

    /**
     * convert dp to px
     */
    public static float dpToPx(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    /**
     * convert px to dp
     */
    public static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
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
                ViewHelper.setScaleX(view,scale);
                ViewHelper.setScaleY(view,scale);
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
}
