package com.github.florent37.materialviewpager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by florentchampigny on 25/04/15.
 * <p/>
 * MaterialViewPagerHelper attach a MaterialViewPagerAnimator to an activity
 * You can use MaterialViewPagerHelper to retrieve MaterialViewPagerAnimator from context
 * Or register a scrollable to the current activity's MaterialViewPagerAnimator
 */
public class MaterialViewPagerHelper {

    private static ConcurrentHashMap<Object, MaterialViewPagerAnimator> hashMap = new ConcurrentHashMap<>();

    /**
     * Register an MaterialViewPagerAnimator attached to an activity into the ConcurrentHashMap
     *
     * @param context  the context
     * @param animator the current MaterialViewPagerAnimator
     */
    static void register(Context context, MaterialViewPagerAnimator animator) {
        hashMap.put(context, animator);
    }

    static void unregister(Context context) {
        if (context != null) {
            hashMap.remove(context);
        }
    }

    /**
     * Register a RecyclerView to the current MaterialViewPagerAnimator
     * Listen to RecyclerView.OnScrollListener so give to $[onScrollListener] your RecyclerView.OnScrollListener if you already use one
     * For loadmore or anything else
     *
     * @param context      current context
     * @param recyclerView the scrollable
     */
    public static void registerRecyclerView(Context context, RecyclerView recyclerView) {
        if (context != null && hashMap.containsKey(context)) {
            final MaterialViewPagerAnimator animator = hashMap.get(context);
            if (animator != null) {
                animator.registerRecyclerView(recyclerView);
            }
        }
    }

    /**
     * Register a ScrollView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     *
     * @param context    current context
     * @param mScrollView the scrollable
     */
    public static void registerScrollView(Context context, NestedScrollView mScrollView) {
        if (context != null && hashMap.containsKey(context)) {
            final MaterialViewPagerAnimator animator = hashMap.get(context);
            if (animator != null) {
                animator.registerScrollView(mScrollView);
            }
        }
    }

    /**
     * Retrieve the current MaterialViewPagerAnimator used in this context (Activity)
     *
     * @param context the context
     * @return current MaterialViewPagerAnimator
     */
    public static MaterialViewPagerAnimator getAnimator(Context context) {
        return hashMap.get(context);
    }

}
