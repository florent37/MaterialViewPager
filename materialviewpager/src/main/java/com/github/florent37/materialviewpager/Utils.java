package com.github.florent37.materialviewpager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

import android.support.v4.view.ViewCompat;

import java.util.List;

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
    static float pxToDp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /*
     * Create a color from [$color].RGB and then add an alpha with 255*[$percent]
     */
    static int colorWithAlpha(int color, float percent) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int alpha = Math.round(percent * 255);

        return Color.argb(alpha, r, g, b);
    }

    static float minMax(float min, float value, float max) {
        value = Math.min(value, max);
        value = Math.max(min, value);
        return value;
    }

    /**
     * modify the scale of multiples views
     *
     * @param scale the new scale
     * @param views
     */
    static void setScale(float scale, View... views) {
        for (View view : views) {
            if (view != null) {
                ViewCompat.setScaleX(view, scale);
                ViewCompat.setScaleY(view, scale);
            }
        }
    }

    /**
     * modify the elevation of multiples views
     *
     * @param elevation the new elevation
     * @param views
     */
    static void setElevation(float elevation, View... views) {
        for (View view : views) {
            if (view != null) {
                ViewCompat.setElevation(view, elevation);
            }
        }
    }

    /**
     * modify the backgroundcolor of multiples views
     *
     * @param color the new backgroundcolor
     * @param views
     */
    static void setBackgroundColor(int color, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setBackgroundColor(color);
            }
        }
    }

    static boolean canScroll(View view) {
        if (view instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) view;
            View child = scrollView.getChildAt(0);
            if (child != null) {
                int childHeight = child.getHeight();
                return scrollView.getHeight() < childHeight + scrollView.getPaddingTop() + scrollView.getPaddingBottom();
            }
            return false;
        } else if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            int yOffset = recyclerView.computeVerticalScrollOffset();
            return yOffset != 0;
        }
        return true;
    }

    public static void scrollTo(Object scroll, float yOffset) {
        if (scroll instanceof RecyclerView) {
            //RecyclerView.scrollTo : UnsupportedOperationException
            //Moved to the RecyclerView.LayoutManager.scrollToPositionWithOffset
            //Have to be instanceOf RecyclerView.LayoutManager to work (so work with RecyclerView.GridLayoutManager)
            final RecyclerView.LayoutManager layoutManager = ((RecyclerView) scroll).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                linearLayoutManager.scrollToPositionWithOffset(0, (int) -yOffset);
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                staggeredGridLayoutManager.scrollToPositionWithOffset(0, (int) -yOffset);
            }
        } else if (scroll instanceof NestedScrollView) {
            ((NestedScrollView) scroll).scrollTo(0, (int) yOffset);
        }
    }

    static View getTheVisibileView(List<View> viewList) {
        Rect scrollBounds = new Rect();

        int listSize = viewList.size();
        for (int i = 0; i < listSize; ++i) {
            View view = viewList.get(i);
            if (view != null) {
                view.getHitRect(scrollBounds);
                if (view.getLocalVisibleRect(scrollBounds)) {
                    return view;
                }
            }
        }
        return null;
    }
}
