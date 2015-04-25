package com.githug.florent37.materialviewpager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by florentchampigny on 25/04/15.
 */
public class MaterialViewPager {

    private static ConcurrentHashMap<Object,MaterialViewPagerAnimator> hashMap = new ConcurrentHashMap<>();

    public static void register(Activity activity, MaterialViewPagerAnimator animator){
        if(!hashMap.containsKey(activity))
            hashMap.put(activity,animator);
    }

    public static void registerRecyclerView(Activity activity, RecyclerView recyclerView, RecyclerView.OnScrollListener onScrollListener){
        if(activity != null && hashMap.containsKey(activity)){
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if(animator != null){
                animator.registerRecyclerView(recyclerView,onScrollListener);
            }
        }
    }

    public static void registerWebView(Activity activity, ObservableWebView webView, ObservableScrollViewCallbacks observableScrollViewCallbacks){
        if(activity != null && hashMap.containsKey(activity)){
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if(animator != null){
                animator.registerWebView(webView, observableScrollViewCallbacks);
            }
        }
    }

}
