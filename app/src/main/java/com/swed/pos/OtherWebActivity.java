package com.swed.pos;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.imagpay.enums.CardDetected;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.WebViewManager;

import java.util.HashMap;


public class OtherWebActivity extends BaseActivity {


    private WebView mWeb;

    @Override
    public void initData(Bundle paramBundle) {

    }

    public void initView() {
        mWeb = findViewById(R.id.web);
        WebViewManager.initCommonAttributes(mWeb, this);
        mWeb.loadUrl("http://120.78.92.46");
        mWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("flag", "url = " + url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mWeb.loadUrl("javascript:app()");//JS代码要是带参数
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                Log.e("flag", "Cookies = " + CookieStr);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWeb != null) {
            mWeb.destroy();
        }
    }

    @Override
    public int setContentView() {
        return R.layout.base_web;
    }

    @Override
    public int setTitle() {
        return 0;
    }


    @Override
    public void cardDetected(CardDetected cardDetected) {

    }

    @Override
    public void returnCardInfo(HashMap hashMap) {

    }

    @Override
    public void swipCardState(int i) {

    }
}
