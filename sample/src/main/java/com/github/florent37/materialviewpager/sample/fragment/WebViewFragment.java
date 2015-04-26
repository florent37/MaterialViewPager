package com.github.florent37.materialviewpager.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.sample.R;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class WebViewFragment extends Fragment {

    private ObservableWebView mWebView;

    public static WebViewFragment newInstance() {
        return new WebViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView = (ObservableWebView) view.findViewById(R.id.webView);

        MaterialViewPager.preLoadInjectHeader(mWebView);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                MaterialViewPager.injectHeader(mWebView,true);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.loadUrl("http://mobile.francetvinfo.fr/");

        MaterialViewPager.registerWebView(getActivity(),mWebView,null);
    }
}
