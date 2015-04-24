package com.githug.florent37.materialviewpager;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class Utils {

    public static boolean isLolipop(){
        return (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public static float dpToPx(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

}
