package com.github.florent37.materialviewpager;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by florentchampigny on 25/04/15.
 */
public class MaterialViewPagerHelper {

    private static ConcurrentHashMap<Object, MaterialViewPagerAnimator> hashMap = new ConcurrentHashMap<>();

    public static void register(Activity activity, MaterialViewPagerAnimator animator) {
        if (!hashMap.containsKey(activity))
            hashMap.put(activity, animator);
    }

    public static void registerRecyclerView(Activity activity, RecyclerView recyclerView, RecyclerView.OnScrollListener onScrollListener) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerRecyclerView(recyclerView, onScrollListener);
            }
        }
    }

    public static void registerWebView(Activity activity, ObservableWebView webView, ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerWebView(webView, observableScrollViewCallbacks);
            }
        }
    }

    @Deprecated
    public static void registerListView(Activity activity, ObservableListView listView, ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerListView(listView, observableScrollViewCallbacks);
            }
        }
    }

    public static void registerScrollView(Activity activity, ObservableScrollView mScrollView, ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerScrollView(mScrollView, observableScrollViewCallbacks);
            }
        }
    }

    public static MaterialViewPagerAnimator getAnimator(Context context) {
        return hashMap.get(context);
    }

    public static void injectHeader(final WebView webView, boolean withAnimation) {
        if (webView != null) {

            MaterialViewPagerAnimator animator = MaterialViewPagerHelper.getAnimator(webView.getContext());
            if (animator != null) {

                WebSettings webSettings = webView.getSettings();
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

                {

                    final int marginTop = animator.getHeaderHeight() + 10;
                    final String js = String.format("document.body.style.marginTop= \"%dpx\"", marginTop);
                    webView.evaluateJavascript(js, null);
                }

                {
                    final String js = "document.body.style.backround-color= white";
                    webView.evaluateJavascript(js, null);
                }

                if (withAnimation)
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(webView, "alpha", 0, 1).start();
                        }
                    }, 400);
            }
        }
    }

    public static void preLoadInjectHeader(WebView mWebView) {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setVisibility(View.INVISIBLE);
    }

}
