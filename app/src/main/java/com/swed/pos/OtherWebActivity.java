package com.swed.pos;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.imagpay.SwipeHandler;
import com.imagpay.enums.BluetoothType;
import com.imagpay.enums.CardDetected;
import com.jhl.jhlblueconn.BluetoothCommmanager;
import com.mf.mpos.pub.CommEnum;
import com.mf.mpos.pub.Controler;
import com.mf.mpos.pub.EmvTagDef;
import com.mf.mpos.pub.param.ReadCardParam;
import com.mf.mpos.pub.result.ReadCardResult;
import com.mf.mpos.pub.result.ReadPosInfoResult;
import com.mf.mpos.pub.swiper.ISwiper;
import com.mf.mpos.pub.swiper.ISwiperMsg;
import com.mf.mpos.pub.swiper.SwiperInfo;
import com.swed.pos.common.BaseCommon;
import com.swed.pos.common.ReceivablesCommon;
import com.swed.pos.model.QueryResult;
import com.swed.pos.model.StringBean;
import com.swed.pos.myapplication.R;
import com.swed.pos.net.BusinessAPI;
import com.swed.pos.net.OkHttpManager;
import com.swed.pos.util.DialogUtil;
import com.swed.pos.util.MyToast;
import com.swed.pos.util.WebViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class OtherWebActivity extends BaseActivity {


    private WebView mWeb;
    private String token;
    private boolean swipCard = false;
    private int classify; //1为中磁  2//Jhl 3//魔方
    public final int ZC = 1;
    public final int JHL = 2;
    public final int MF = 3;
    public final int MFBLACK = 4;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {//扫描zc设备
                classify = ZC;
                if (isOpen) {
                    BaseCommon.searchDevice(OtherWebActivity.this, true);
                } else {
                    showBlueWarn();
                }
            } else if (msg.what == 2) {//中磁刷卡操作
                ReceivablesCommon.startSwippingCard(OtherWebActivity.this, "", classify);
            } else if (msg.what == 3) {//扫描JHL
                classify = JHL;
                if (isOpen) {
                    BaseCommon.searchDevice(OtherWebActivity.this, false);
                } else {
                    showBlueWarn();
                }
            } else if (msg.what == 4) {//jhl刷卡操作
                ReceivablesCommon.startSwippingCard(OtherWebActivity.this, (String) msg.obj, classify);
            } else if (msg.what == 5) {//jhl绑定sn号
                jhlSn = (String) msg.obj;
                bindSn((String) msg.obj);
            } else if (msg.what == 6) {//扫描魔方
                classify = MF;
                if (isOpen) {
                    BaseCommon.searchMfDevice(OtherWebActivity.this, btAdapter, handler);
                } else {
                    showBlueWarn();
                }
            } else if (msg.what == 7) {//魔方刷卡操作
                ReceivablesCommon.startSwippingCard(OtherWebActivity.this, (String) msg.obj, classify);
            } else if (msg.what == 8) {//魔方连接成功绑定sn号
                if (Controler.posConnected()) {
                    onBlueState(1);
                    ReadPosInfoResult result = Controler.ReadPosInfo2();
                    if (result.commResult.equals(CommEnum.COMMRET.NOERROR)) {
                        dismissDialog();
                        mFSn = result.sn;
                        bindSn(result.sn);
                        System.out.print("sn=========" + result.sn);
                    }
                } else {
                    dismissDialog();
                    Toast.makeText(MyApplication.getInstace(), R.string.connect_bluetooth_error, Toast.LENGTH_LONG).show();
                }
            } else if (msg.what == 9) {//黑色魔方刷卡
                classify = MFBLACK;
                ReceivablesCommon.startSwippingCard(OtherWebActivity.this, (String) msg.obj, MFBLACK);
            } else if (msg.what == 10) {
                showBlueWarn();
            }
        }
    };

    //判断蓝牙是否开启的提示框
    private void showBlueWarn() {
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                new AlertDialog.Builder(this).setTitle(R.string.place_open_bluetooth).setIcon(R.mipmap.ic_launcher).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, final int paramAnonymousInt) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(enableBtIntent);
                    }
                }).setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    }
                }).create().show();

            }
        } else {
            MyToast.show(this, "该手机不支持蓝牙");
        }

    }

    public String pwd;
    private String jhlSn;
    private String mFSn;
    private SharedPreferences config;

    @Override
    public void initData(Bundle paramBundle) {
        getH5Url();
    }

    public void getH5Url() {
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
        IntentFilter statusFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        mContext.registerReceiver(mStatusReceive, statusFilter);
        //魔方 start
        btAdapter.enable();
        config = getSharedPreferences("config", Context.MODE_PRIVATE);
        CommEnum.CONNECTMODE mode = getMode();
        Controler.Init(this, mode, getcsid());
//        Controler.Init(this, CommEnum.CONNECTMODE.BLUETOOTH);
        //魔方 end

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


    private void bindSn(String sn) {
//        String action = "http://upay.xmktv.net?g=app&m=public&a=buildSn";
        String bindSn = "javascript:get_buildSn_api(%s)";
        bindSn = String.format(bindSn, "'" + sn + "'");
        mWeb.loadUrl(bindSn);
    }

    private void commitData(String asciipwd, HashMap<String, String> hashMap) {
        String bindSn = "javascript:get_consume_api(%s,%s,%s,%s,%s,%s)";
        if (classify == 1) {
            bindSn = String.format(bindSn, "'" + asciipwd + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("PAN") + "'", "'" + hashMap.get("exp") + "'", "'" + BaseActivity.settings.getSN() + "'");
        } else if (classify == 3 || classify == 4) {
            bindSn = String.format(bindSn, "'" + asciipwd + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("PAN") + "'", "'" + hashMap.get("ExpireDate") + "'", "'" + mFSn + "'");
        } else {
            bindSn = String.format(bindSn, "'" + asciipwd + "'", "'" + hashMap.get("Encrytrack2") + "'", "'" + hashMap.get("Track2") + "'", "'" + hashMap.get("PAN") + "'", "'" + hashMap.get("ExpireDate") + "'", "'" + jhlSn + "'");
        }
        mWeb.loadUrl(bindSn);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBack();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleBack() {
        if (mWeb.canGoBack()) {
            mWeb.goBack();
            return;
        }
        finish();
    }

    public void startSwippingCard(final String money) {
        this.swipCard = false;
        showDialogRes(R.string.place_swipping_card);
        //0.01金额
//        this.bluetoothComm.MagnCard(15000L, (long) (0.01 * 100.0F), 1);
        if (classify == 2) {
            this.bluetoothComm.MagnCard(15000L, (long) (Float.valueOf(money) * 100.0F), 1);
        } else if (classify == 3) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    swipCard(Long.valueOf(money));
                }
            }).start();
        } else if (classify == 4) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    swipBlackCard(Long.valueOf(money));
                }
            }).start();
        }
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
                if (classify == 1) {
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
            }
        });
    }

    public void disconnect() {
        onBlueState(2);
        System.out.println("-------disconnect------");
    }

    //中磁展示密码框
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
            hashMap.put("exp:", exp);
            System.out.println("map---->" + hashMap);
            runOnUiThread(new Runnable() {
                public void run() {
//                    if (!ReceivablesCommon.checkData(SelectTypeActivity.this)) {
//                        return;
//                    }
//                    ReceivablesActivity.this.pwd = "";
                    DialogUtil.showPasswordDialog(OtherWebActivity.this, R.string.place_input_password, 2, new DialogUtil.OnMoneyInputListener() {
                        public void onMoneyInput(EditText paramAnonymous2EditText) {
                            pwd = paramAnonymous2EditText.getText().toString();
                            commitData(pwd, hashMap);
                        }
                    });
                }
            });
        }
    }

    //=======iMagPay回调函数 end ========


    //=======JHL回调函数 start ========

    //获取jhl的设备信息
    public void onDeviceInfo(final Map<String, String> paramMap) {
        super.onDeviceInfo(paramMap);
        Message message = new Message();
        message.what = 5;
        message.obj = paramMap.get("SN");
        handler.sendMessage(message);
    }

    public void onResult(final int paramInt1, final int paramInt2, final String paramString) {
        System.out.println("onResult------>" + paramInt1 + "---" + paramInt2 + "---" + paramString);
        runOnUiThread(new Runnable() {
            public void run() {
                switch (paramInt1) {
                    default:
                        break;
                    case 80:
                        //jhl连接是否成功判断
                        showToast(paramString.toString());
                        break;
                    case 18:
                    case 24:
                        if (paramInt2 == 0) {
                            ReceivablesCommon.showPasswordInputDialog(OtherWebActivity.this);
                            break;
                        }
                        showToast("刷卡失败,错误代码:" + Integer.toString(paramInt2));
                        break;
                    case 52:
                        if (paramInt2 == 0) {
                            showToast("主密钥设置成功");
                            break;
                        }
                        showToast("主密钥设置失败,错误代码:" + Integer.toString(paramInt2));
                        break;
                    case 56:
                        if (paramInt2 == 0) {
                            showToast("工作密钥设置成功");
                            break;
                        }
                        showToast("工作密钥设置失败,错误代码:" + Integer.toString(paramInt2));
                        break;
                    case 55:
                        if (paramInt2 == 0) {
                            showToast("MAC:" + paramString.toString());
                            break;
                        }
                        showToast("GetMac失败,错误代码:" + Integer.toString(paramInt2));
                        break;
                    case 66:
                        if (paramInt2 == 0) {
                            showToast("商户号终端号设置成功");
                            showToast("正在读取TerNo...");
                            bluetoothComm.ReadTernumber();
                            break;
                        }
                        showToast("商户号终端号设置失败,错误代码:" + Integer.toString(paramInt2));
                        break;
                    case 65:
                        if (paramInt2 == 0) {
                            showToast("商户号终端号:" + paramString.toString());
                            break;
                        }
                        showToast("商户号终端号读取失败:" + Integer.toString(paramInt2));
                }
            }
        });
    }

    @Override
    public void swipCardState(int paramInt) {
        // TODO Auto-generated method stub
        String strTips = "";
        switch (paramInt) {
            case BluetoothCommmanager.SWIPE_SUCESS:
                strTips = strTips + "刷卡正常";
                break;
            case BluetoothCommmanager.SWIPE_DOWNGRADE:
                strTips = strTips + "刷卡降级";
                break;
            case BluetoothCommmanager.SWIPE_ICCARD_INSETR:
                strTips = strTips + "在待机界面插入IC卡";
                break;
            case BluetoothCommmanager.SWIPE_ICCARD_SWINSETR:
                strTips = strTips + "交易功能插入IC";
                break;
            case BluetoothCommmanager.SWIPE_WAIT_BRUSH:
                strTips = strTips + "请刷卡,等待刷卡";
                break;
            case BluetoothCommmanager.SWIPE_CANCEL:
                strTips = strTips + ":用户取消";
                break;
            case BluetoothCommmanager.SWIPE_TIMEOUT_STOP:
                strTips = strTips + ":超时退出";
                break;
            case BluetoothCommmanager.SWIPE_IC_FAILD:
                strTips = strTips + ":IC卡处理数据失败";
                break;
            case BluetoothCommmanager.SWIPE_NOICPARM:
                strTips = strTips + ":无IC卡参数";
                break;
            case BluetoothCommmanager.SWIPE_STOP:
                strTips = strTips + ":交易终止";
                break;
            case BluetoothCommmanager.SWIPE_IC_REMOVE:
                strTips = strTips + ":加密失败,用户拔出IC卡";
                break;
            case BluetoothCommmanager.SWIPE_LOW_POWER:
                strTips = strTips + ":低电量,不允许交易";
                break;
            case BluetoothCommmanager.BLUE_POWER_OFF:
                strTips = strTips + ":已关机";
                break;
            case BluetoothCommmanager.BLUE_SDK_ERROR:
                strTips = strTips + ":SDK文件丢失,SDK异常";
                break;
            case BluetoothCommmanager.BLUE_DEVICE_ERROR:
                strTips = strTips + ":设备不合法";
                break;
            default:
                break;
        }
        System.out.print(strTips);
    }

    @Override
    public void onReadCardData(final Map paramMap) {
        System.out.println("onReadCardData------->");
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry localEntry = (Map.Entry) iterator.next();
            System.out.println(localEntry.getKey() + "------" + localEntry.getValue());
        }
        dismissDialog();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                commitData(pwd, (HashMap<String, String>) paramMap);
            }
        });

    }

    //=======JHL回调函数 end ========

    //=============魔方start========
    //魔方手刷

    public void swipCard(long money) {
        ISwiper isw = Controler.getISwiper(new ISwiperMsg() {
            @Override
            public void onShowMsg(String msg) {
            }
        });

        //100为金额
        SwiperInfo sinfo = isw.StartCSwiper(money, CommEnum.TRANSTYPE.FUNC_SALE, 60, false);
//        StringBuilder sb = new StringBuilder();
        HashMap<String, String> hashMap = new HashMap<>();
//        sb.append(GetString(R.string.commResult) + ":" + sinfo.result.name() + "\n");
//        sb.append(getString(R.string.sinfo_ismag) + ":" + (sinfo.isMag ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append(getString(R.string.sinfo_isForceic) + ":" + (sinfo.isForceic ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append(getString(R.string.sinfo_isIcCard) + ":" + (sinfo.isIcCard ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append(getString(R.string.sinfo_isRfid) + ":" + (sinfo.isRfid ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append(GetString(R.string.fallback) + ":" + (sinfo.isFallBack ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append("PAN" + ":" + sinfo.sPan + "\n");
//        sb.append(GetString(R.string.expData) + ":" + sinfo.sExpData + "\n");
//        sb.append(GetString(R.string.serviceCode) + ":" + sinfo.serviceCode + "\n");
//        sb.append(GetString(R.string.track2Len) + ":" + sinfo.track2Len + "\n");
//        sb.append(GetString(R.string.track2) + ":" + sinfo.sTrack2 + "\n");
//        sb.append(GetString(R.string.track3Len) + ":" + sinfo.track3Len + "\n");
//        sb.append(GetString(R.string.track3) + ":" + sinfo.sTrack3 + "\n");
        //sb.append( "数据随机数:" + sinfo.randomdata  + "\n" );
//        sb.append(GetString(R.string.pansn) + ":" + sinfo.pansn + "\n");
//        sb.append(GetString(R.string.icData) + ":" + Misc.hex2asc(sinfo.tlvData) + "\n");
        //sb.append( "MAC:" + Misc.hex2asc(sinfo.mac )  + "\n"   );

//        msg = sb.toString();
        hashMap.put("PAN", sinfo.sPan);
        hashMap.put("Track2", sinfo.sTrack2);
        hashMap.put("ExpireDate", sinfo.sExpData);

        if (Controler.GetMode() == CommEnum.CONNECTMODE.BLE) {
            Controler.disconnectPos();
        }


        blockmsg(hashMap);
    }

    public void swipBlackCard(long money) {
        ReadCardParam param = new ReadCardParam();
        //设置金额 单位分
        param.setAmount(money);
        //设置交易类型
        param.setTransType(CommEnum.TRANSTYPE.FUNC_SALE);
        //----------------------可选参数------------------
        //启用输入PIN
        param.setPinInput(true);
        param.setOnSteplistener(new ReadCardParam.onStepListener() {
            @Override
            public void onStep(byte step) {
                switch (step) {
                    case 1://等待刷卡
                        break;
                    case 2://正在读卡
                        break;
                    case 3://等待用户输入密码
                        break;
                }
            }
        });
        //订单号
        param.setOrderid("00000001281058E7");
        //流水号
        param.setLsh("000001");
        List<byte[]> tags = new ArrayList<>();
        tags.add(EmvTagDef.EMV_TAG_9F33_TM_CAP);
        tags.add(EmvTagDef.EMV_TAG_95_TM_TVR);
        tags.add(EmvTagDef.EMV_TAG_9F37_TM_UNPNUM);
        tags.add(EmvTagDef.EMV_TAG_9F1E_TM_IFDSN);
        tags.add(EmvTagDef.EMV_TAG_9F10_IC_ISSAPPDATA);
        tags.add(EmvTagDef.EMV_TAG_9F26_IC_AC);
        tags.add(EmvTagDef.EMV_TAG_82_IC_AIP);
        tags.add(EmvTagDef.EMV_TAG_DF31_IC_IISSCRIRES);
        tags.add(EmvTagDef.EMV_TAG_9F1A_TM_CNTRYCODE);
        tags.add(EmvTagDef.EMV_TAG_9A_TM_TRANSDATE);
        //param.setTags( tags );
        //------------------------------------------------

        //执行MPOS读卡流程
        ReadCardResult ret = Controler.ReadCard(param);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("PAN", ret.pan);
        hashMap.put("Track2", ret.track2);
        hashMap.put("ExpireDate", ret.expData);
        blockmsg(hashMap);
        //操作结果

//        sb.append(GetString(R.string.commResult) + ":" + ret.commResult.toDisplayName() + "\n");
//        sb.append(GetString(R.string.getCartTypeName) + ":" + ret.getCartTypeName() + "\n");
//        sb.append(GetString(R.string.fallback) + ":" + (ret.fallback ? GetString(R.string.yes) : GetString(R.string.no)) + "\n");
//        sb.append(GetString(R.string.pan) + ":" + ret.pan + "\n");
//        sb.append(GetString(R.string.expData) + ":" + ret.expData + "\n");
//        sb.append(GetString(R.string.serviceCode) + ":" + ret.serviceCode + "\n");
//        sb.append(GetString(R.string.track2Len) + ":" + ret.track2Len + "\n");
//        sb.append(GetString(R.string.track2) + ":" + ret.track2 + "\n");
//        sb.append(GetString(R.string.track3Len) + ":" + ret.track3Len + "\n");
//        sb.append(GetString(R.string.track3) + ":" + ret.track3 + "\n");
//        //	sb.append( GetString(R.string.randomdata) + ":"  + ret.randomdata  + "\n" );
//        sb.append(GetString(R.string.pansn) + ":" + ret.pansn + "\n");
//        sb.append(GetString(R.string.icData) + ":" + ret.icData + "\n");
//        sb.append(GetString(R.string.pinLen) + ":" + ret.pinLen + "\n");
//        sb.append(GetString(R.string.pinblock) + ":" + ret.pinblock + "\n");
//
//        msg = sb.toString();
    }


    //魔方展示密码框
    public void blockmsg(final HashMap<String, String> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.showPasswordDialog(OtherWebActivity.this, R.string.place_input_password, 2, new DialogUtil.OnMoneyInputListener() {
                    public void onMoneyInput(EditText paramAnonymous2EditText) {
                        pwd = paramAnonymous2EditText.getText().toString();
                        commitData(pwd, hashMap);
                    }
                });
                dismissDialog();
            }
        });
    }


    CommEnum.CONNECTMODE getMode() {
        int connectmode = config.getInt("CONNECTMODE", CommEnum.CONNECTMODE.BLUETOOTH.ordinal());
        CommEnum.CONNECTMODE mode = CommEnum.CONNECTMODE.values()[connectmode];
        return mode;
    }

    int getcsid() {
        //厂商id
        int connectmode = config.getInt("CSID", 0);
        return connectmode;
    }

    //=========魔方end====


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

        @JavascriptInterface
        public void searchJhlDevice() {
            handler.sendEmptyMessage(3);
        }

        @JavascriptInterface
        public void swipJhlCard(String money) {
            Message msg = new Message();
            msg.what = 4;
            msg.obj = money;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void searchMfDevice() {
            handler.sendEmptyMessage(6);
        }

        @JavascriptInterface
        public void swipMfCard(String money) {
            Message msg = new Message();
            msg.what = 7;
            msg.obj = money;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void swipMfBlackCard(String money) {
            Message msg = new Message();
            msg.what = 9;
            msg.obj = money;
            handler.sendMessage(msg);
        }

        @JavascriptInterface
        public void showBlueWarn() {
            handler.sendEmptyMessage(10);
        }

    }

    @Override
    public void onBlueState(int paramInt) {
        super.onBlueState(paramInt);
    }

    private boolean isOpen;
    private BroadcastReceiver mStatusReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                        case BluetoothAdapter.STATE_ON:
                            isOpen = true;
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            isOpen = false;
                            break;
                    }
                    break;
            }
        }
    };

}

