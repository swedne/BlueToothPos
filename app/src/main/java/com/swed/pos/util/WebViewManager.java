package com.swed.pos.util;

import android.app.Activity;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewManager {

    /**
     * 初始化通用参数
     *
     * @param webView
     */
    public static void initCommonAttributes(WebView webView, Activity activity) {
        WebSettings settings = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(true);
        webView.setVerticalScrollBarEnabled(true);

        settings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        settings.setAllowFileAccess(true); // 允许访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口

        /**
         * 缩放处理
         */
        settings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        settings.setBuiltInZoomControls(true); //设置内置的缩放控件。 这个取决于setSupportZoom(), 若setSupportZoom(false)，则该WebView不可缩放，这个不管设置什么都不能缩放。
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        /**
         * 内容布局
         */
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setSupportMultipleWindows(false);//设置WebView是否支持多屏窗口

        /**
         * 文件缓存
         */
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//设置缓存模式
        settings.setAppCacheMaxSize(Long.MAX_VALUE);


        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        settings.setGeolocationEnabled(true);
        settings.setAppCachePath(activity.getDir("appcache", 0).getPath());
        settings.setDatabasePath(activity.getDir("databases", 0).getPath());
//        settings.setGeolocationDatabasePath(activity.getDir("geolocation", 0).getPath());
//        settings.setPageCacheCapacity(IX5settingss.DEFAULT_CACHE_CAPACITY);
    }


    public static class CommonWebViewClient extends WebViewClient {

        private Activity mActivity;

        public CommonWebViewClient(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}
