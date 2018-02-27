package com.swed.pos;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.imagpay.SwipeHandler;
import com.imagpay.enums.BluetoothType;
import com.imagpay.enums.CardDetected;
import com.swed.pos.common.BaseCommon;
import com.swed.pos.common.ReceivablesCommon;
import com.swed.pos.model.QueryResult;
import com.swed.pos.model.StringBean;
import com.swed.pos.myapplication.R;
import com.swed.pos.net.BusinessAPI;
import com.swed.pos.net.OkHttpManager;
import com.swed.pos.util.DialogUtil;
import com.swed.pos.util.WebViewManager;

import java.util.HashMap;


public class OtherWebActivity extends BaseActivity {


    private WebView mWeb;
    private String token;
    private boolean swipCard = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//扫描设备
                BaseCommon.searchDevice(OtherWebActivity.this, true);
            } else if (msg.what == 2) {//刷卡操作
                ReceivablesCommon.startSwippingCard(OtherWebActivity.this);
            }
        }
    };

    @Override
    public void initData(Bundle paramBundle) {
        BusinessAPI.getUrl(new OkHttpManager.HttpCallback(StringBean.class) {
            @Override
            public void onResponse(QueryResult result) {
                StringBean stringBean = (StringBean) result;
                mWeb.loadUrl(stringBean.getDataMsg().getUrl());

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public void initView() {
        if (bluetoothHandler != null) {
            bluetoothHandler.addBluetoothListener(this);
        }
        mWeb = findViewById(R.id.web);
        WebViewManager.initCommonAttributes(mWeb, this);
        mWeb.addJavascriptInterface(new JavaScriptLocalObj(), "local_obj");
        mWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//                Uri uri = Uri.parse(url);
//                String className = uri.getHost();
//                String param = uri.getQuery();
//                String port = uri.getPort() + "";
//                String path = uri.getPath();

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        mWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
    public void returnCardInfo(HashMap hashMap) {

    }

    @Override
    public void swipCardState(int i) {

    }

    private void bindSn(String sn) {
        String action = "http://upay.xmktv.net?g=app&m=public&a=buildSn";
        String bindSn = "javascript:get_buildSn_api(%s,%s)";
        bindSn = String.format(bindSn, "'" + action + "'", "'" + sn + "'");
        mWeb.loadUrl(bindSn);
    }

    private void commitData(String asciipwd, HashMap<String, String> hashMap) {
        String bindSn = "javascript:get_consume_api(%s,%s,%s,%s)";
        bindSn = String.format(bindSn, "'" + asciipwd + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("Track2") + "'", "'" + BaseActivity.settings.getSN() + "'");
        mWeb.loadUrl(bindSn);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);

    }

    public void startSwippingCard() {
        this.swipCard = false;
        showDialogRes(R.string.place_swipping_card);
        //0.01金额
//        this.bluetoothComm.MagnCard(15000L, (long) (0.01 * 100.0F), 1);
    }

    public void setSwipCard(boolean paramBoolean) {
        this.swipCard = paramBoolean;
    }

    //=======iMagPay回调函数 start ========
    public void connected(BluetoothType paramBluetoothType) {
        super.connected(paramBluetoothType);
        type = paramBluetoothType;
        runOnUiThread(new Runnable() {
            public void run() {
                OtherWebActivity.this.onBlueState(1);
                BaseActivity.settings = BaseActivity.bluetoothHandler.shareSettingsInstance();
                String str = BaseActivity.settings.getSN();
                HashMap localHashMap = new HashMap();
                localHashMap.put("SN", str);
                //绑定sn号
                bindSn(str);
                System.out.print("SN=============" + str);
                OtherWebActivity.this.showToast(R.string.band_bluetooth_success);
                OtherWebActivity.this.onDeviceInfo(localHashMap);
            }
        });
    }

    public void disconnect() {
        onBlueState(2);
        System.out.println("-------disconnect------");
    }

    @Override
    public void cardDetected(final CardDetected paramCardDetected) {
        dismissDialog();
        Object localObject = null;
        String str1;
        String str2;
        String str3;
        String str4;
        String exp;
        if (type == BluetoothType.BLE) {
            localObject = bluetoothHandler.shareBleInstance();
            System.out.println("type----ble");
        } else if (type == BluetoothType.SPP) {
            localObject = bluetoothHandler.shareSppInstance();
            System.out.println("type----spp");
        }
        if (paramCardDetected == CardDetected.SWIPED) {
            final HashMap<String, String> hashMap = new HashMap();
            str1 = ((SwipeHandler) localObject).getMagPan();
            str2 = ((SwipeHandler) localObject).getTrack1Data();
            str3 = ((SwipeHandler) localObject).getTrack2Data();
            str4 = ((SwipeHandler) localObject).getTrack3Data();
            exp = ((SwipeHandler) localObject).getMagExpDate();
            hashMap.put("PAN:", str1);
            hashMap.put("Track1", str2);
            hashMap.put("Track2", str3);
            hashMap.put("Track3", str4);
            hashMap.put("有效期:", exp);
            System.out.println("map---->" + hashMap);
            runOnUiThread(new Runnable() {
                public void run() {
//                    if (!ReceivablesCommon.checkData(SelectTypeActivity.this)) {
//                        return;
//                    }
//                    ReceivablesActivity.this.pwd = "";
                    DialogUtil.showPasswordDialog(OtherWebActivity.this, R.string.place_input_password, 2, new DialogUtil.OnMoneyInputListener() {
                        public void onMoneyInput(EditText paramAnonymous2EditText) {
                            String pwd = paramAnonymous2EditText.getText().toString();
                            commitData(pwd, hashMap);
//                            onReadCardData(hashMap);
                        }
                    });
                }
            });
        }
    }

    //=======iMagPay回调函数 end ========


    public class JavaScriptLocalObj {
//        @JavascriptInterface
//        public void getToken(String token) {
//            Toast.makeText(MyApplication.getInstace(), token, Toast.LENGTH_LONG).show();
//            OtherWebActivity.this.token = token;
//            Log.e("flag", "token==========" + token);
//        }

        @JavascriptInterface
        public void searchDevice() {
            handler.sendEmptyMessage(1);
        }

        @JavascriptInterface
        public void swipCard() {
            handler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onBlueState(int paramInt) {
        if (paramInt == 1) {
            dismissDialog();
        }
        super.onBlueState(paramInt);
    }

}

