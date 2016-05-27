package com.github.florent37.materialviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;

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
    public static void register(Context context, MaterialViewPagerAnimator animator) {
        hashMap.put(context, animator);
    }

    public static void unregister(Context context) {
        if (context != null) {
            hashMap.remove(context);
        }
    }

    /**
     * Register a RecyclerView to the current MaterialViewPagerAnimator
     * Listen to RecyclerView.OnScrollListener so give to $[onScrollListener] your RecyclerView.OnScrollListener if you already use one
     * For loadmore or anything else
     *
     * @param context          current context
     * @param recyclerView     the scrollable
     * @param onScrollListener use it if you want to get a callback of the RecyclerView
     */
    public static void registerRecyclerView(Context context, RecyclerView recyclerView) {
        if (context != null && hashMap.containsKey(context)) {
            MaterialViewPagerAnimator animator = hashMap.get(context);
            if (animator != null) {
                animator.registerRecyclerView(recyclerView);
            }
        }
    }

    /**
     * Register a WebView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     *
     * @param activity                      current context
     * @param webView                       the scrollable
     * @param observableScrollViewCallbacks use it if you want to get a callback of the RecyclerView
     */
    @Deprecated
    public static void registerWebView(Activity activity, ObservableWebView webView, ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerWebView(webView, observableScrollViewCallbacks);
            }
        }
    }

    /**
     * Register a ScrollView to the current MaterialViewPagerAnimator
     * Listen to ObservableScrollViewCallbacks so give to $[observableScrollViewCallbacks] your ObservableScrollViewCallbacks if you already use one
     * For loadmore or anything else
     *
     * @param activity                      current context
     * @param mScrollView                   the scrollable
     * @param observableScrollViewCallbacks use it if you want to get a callback of the RecyclerView
     */
    public static void registerScrollView(Activity activity, ObservableScrollView mScrollView, ObservableScrollViewCallbacks observableScrollViewCallbacks) {
        if (activity != null && hashMap.containsKey(activity)) {
            MaterialViewPagerAnimator animator = hashMap.get(activity);
            if (animator != null) {
                animator.registerScrollView(mScrollView, observableScrollViewCallbacks);
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

    /**
     * Have to be called from WebView.WebViewClient.onPageFinished
     * ex : mWebView.setWebViewClient(new WebViewClient() { onPageFinished(WebView view, String url) { [HERE] }});
     * Inject a header to a webview : add a margin-top="**dpx"
     * Had to have a transparent background with a placeholder on top
     * So inject js for placeholder and setLayerType(WebView.LAYER_TYPE_SOFTWARE, null); for transparency
     * TODO : inject JavaScript for Pre-Lolipop with loadUrl("js:...")
     *
     * @param webView
     * @param withAnimation if true, disapear with a fadein
     */
    @Deprecated
    public static void injectHeader(final WebView webView, boolean withAnimation) {
        if (webView != null) {

            MaterialViewPagerAnimator animator = MaterialViewPagerHelper.getAnimator(webView.getContext());
            if (animator != null) {

                WebSettings webSettings = webView.getSettings();
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);

                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    //transparent background
                    webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                }

                { //inject margin top
                    final int marginTop = animator.getHeaderHeight() + 10;
                    final String js = String.format("document.body.style.marginTop= \"%dpx\"", marginTop);
                    webViewLoadJS(webView, js);
                }

                {
                    final String js = "document.body.style.backround-color= white";
                    webViewLoadJS(webView, js);
                }

                if (withAnimation) {
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.setVisibility(View.VISIBLE);
                            ViewCompat.setAlpha(webView, 0f);
                            ViewCompat.animate(webView).withLayer().alpha(1);
                        }
                    }, 400);
                }
            }
        }
    }

    /**
     * Prepare the webview, set Invisible and transparent background
     * Must call injectHeader next
     */
    @Deprecated
    public static void preLoadInjectHeader(WebView mWebView) {
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.setVisibility(View.INVISIBLE);
    }

    @Deprecated
    private static void webViewLoadJS(WebView webView, String js) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(js, null);
        } else {
            webView.loadUrl("javascript: " + js);
        }
    }

}
