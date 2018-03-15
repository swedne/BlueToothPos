package com.swed.pos;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.imagpay.BluetoothBean;
import com.imagpay.BluetoothHandler;
import com.imagpay.BluetoothListener;
import com.imagpay.ErrMsg;
import com.imagpay.Settings;
import com.imagpay.ble.BleHandler;
import com.imagpay.enums.BluetoothType;
import com.imagpay.enums.PinPadEvent;
import com.jhl.bluetooth.ibridge.BluetoothIBridgeDevice;
import com.jhl.jhlblueconn.BlueCommangerCallback;
import com.jhl.jhlblueconn.BluetoothCommmanager;
import com.swed.pos.common.BaseCommon;
import com.swed.pos.dialog.LodingDialog;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.MyToast;
import com.swed.pos.util.ShareManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public abstract class BaseActivity extends AppCompatActivity implements BlueCommangerCallback, BluetoothListener {
    public static final String DEVICE_ADDRESS = "device_address";
    public static final String[] DEVICE_ADDRESS_FILETER;
    public static int SCREEN_HEIGHT = 0;
    public static int SCREEN_WIDTH = 0;
    public static final long WAIT_TIMEOUT = 15000L;
    public static boolean bOpenDevice = false;
    public static BluetoothHandler bluetoothHandler;
    public static Map<String, String> deviceInfo;
    private static Set<LodingDialog> dias = new HashSet();
    public static Stack<BaseActivity> mLocalStack;
    public static Settings settings;
    private static final int titleTextColor = -1;
    public static BluetoothType type;
    ArrayList<BluetoothIBridgeDevice> arrayList;
    private ImageView backImage;
    List<BluetoothBean> beanList = new ArrayList();
    public static BluetoothCommmanager bluetoothComm = null;
    boolean isDestroyed;
    public boolean isGetSn = false;
    public boolean isShowDialog = false;
    public LodingDialog lodingDialog;
    protected BaseActivity mContext;
    protected Menu menu;
    private Toolbar toolbar;
    private TextView tv_toolbar;

    static {
        DEVICE_ADDRESS_FILETER = null;
        mLocalStack = new Stack();
    }

    private void initLang() {
        Resources localResources = getResources();
        Configuration localConfiguration = localResources.getConfiguration();
        Locale localLocale = Locale.getDefault();
        String str = ShareManager.getInstance(this).getLanguage();
        System.out.println("base----->" + str);
        if ("ch".equals(str)) {
            localLocale = Locale.CHINA;
        }
    }

    private void initProgressDialog(String paramString, boolean paramBoolean) {
        if (isFinishing()) {
            return;
        }
        this.isShowDialog = true;
        if (this.lodingDialog == null) {
            lodingDialog = new LodingDialog(this, paramString, paramBoolean);
            dias.add(this.lodingDialog);
        }
        System.out.println(this + "------------showDialog------------" + this.lodingDialog);
        this.lodingDialog.setMsg(paramString);
        this.lodingDialog.setCancelable(paramBoolean);
        this.lodingDialog.show();
    }

    private void titleTextDefaultSetting() {
        this.toolbar.setTitleTextColor(-1);
    }

//    // imagpay检测到IC卡插拔,以及磁条卡刷卡回调函数
//    public void cardDetected(CardDetected paramCardDetected) {
//        System.out.println("-------cardDetected------" + getLocalClassName());
//        if (type == BluetoothType.BLE) {
//            System.out.println("type----->BLE");
//            if (paramCardDetected == CardDetected.SWIPED) {
//                BleHandler bleHandler = bluetoothHandler.shareBleInstance();
//                HashMap hashMap = new HashMap();
//                String str1 = bleHandler.getMagPan();
//                String str2 = bleHandler.getTrack1Data();
//                String str3 = bleHandler.getTrack2Data();
//                String str4 = bleHandler.getTrack3Data();
//                String exp = bleHandler.getMagExpDate();
//                hashMap.put("PAN", str1);
//                hashMap.put("Track1", str2);
//                hashMap.put("Track2", str3);
//                hashMap.put("Track3", str4);
//                hashMap.put("ExpireDate", exp);
//            }
//        }
////        for (Object localObject = bluetoothHandler.shareBleInstance(); ; localObject = bluetoothHandler.shareSppInstance()) {
////
////            return;
////        }
//    }

    //imgpay的连接回调
    @Override
    public void connected(BluetoothType paramBluetoothType) {
        type = paramBluetoothType;
        onBlueState(1);
        settings = bluetoothHandler.shareSettingsInstance();
    }

    public void disconnect() {
        onBlueState(2);
        System.out.println("-------disconnect------");
    }

    public void disconnectBluetooth() {
        if (bluetoothComm != null) {
            bluetoothComm.DisConnectBlueDevice();
        }
        BleHandler localBleHandler = null;
        if (bluetoothHandler != null) {
            localBleHandler = bluetoothHandler.shareBleInstance();
        }
        try {
            localBleHandler.cancelConnect();
            onBlueState(2);
        } catch (Exception localException) {
        }
    }

    public void dismissDialog() {
        this.isShowDialog = false;
        if (this.lodingDialog != null) {
            this.lodingDialog.dismiss();
            dias.remove(this.lodingDialog);
        }
    }

    public void errMsg(ErrMsg paramErrMsg) {
        MyToast.show(this, "异常：" + paramErrMsg.getMsg());
    }

    //jhl发现设备结果
    public void findReader(BluetoothBean paramBluetoothBean) {
//        Iterator localIterator = this.beanList.iterator();
//        while (localIterator.hasNext()) {
//            BluetoothBean localBluetoothBean = (BluetoothBean) localIterator.next();
//
//            if (paramBluetoothBean.getDevice().getAddress().equals(localBluetoothBean.getDevice().getAddress())) {
//                break;
//            }
//        }
//        this.beanList.add(paramBluetoothBean);
        boolean _tmp = false;
        //搜索到了相同mac的蓝牙设备不添加
        for (BluetoothBean bean : beanList) {
            if (bean.getDevice().getAddress()
                    .equals(paramBluetoothBean.getDevice().getAddress())) {
                _tmp = true;
                break;
            }
        }
        if (!_tmp) {
            beanList.add(paramBluetoothBean);
        }
    }

    public void finishedDiscovery() {
        System.out.println("-------finishedDiscovery------");
        runOnUiThread(new Runnable() {
            public void run() {
                BaseCommon.showDeviceDialog(BaseActivity.this, BaseActivity.bluetoothHandler, BaseActivity.this.beanList);
            }
        });
    }

    public Menu getMenu() {
        return this.menu;
    }


    protected void initBluetooth() {
        if (bluetoothHandler == null) {
            bluetoothHandler = new BluetoothHandler(getApplicationContext());
            // 设置自定义设备前缀
            bluetoothHandler.setSppPrefix("UPAY");
        }
        if (bluetoothComm == null) {
            bluetoothComm = BluetoothCommmanager.getInstance(this, getApplicationContext());
            BluetoothCommmanager.SetEncryMode(1, 3, 0);
        }
    }

    public abstract void initData(Bundle paramBundle);


    public abstract void initView();

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
//        switch (paramInt1) {
//        }
//        do {
//            return;
//        } while (paramInt2 != -1);
//        String sn = paramIntent.getStringExtra("sn");
//        HashMap localHashMap = new HashMap();
//        localHashMap.put("SN", sn);
//        onDeviceInfo(localHashMap);
    }

    public void onBlueState(final int paramInt) {
        System.out.println("onBlueState------->" + paramInt);
        runOnUiThread(new Runnable() {
            public void run() {
                BaseActivity.bOpenDevice = BaseCommon.onBluetoothStatusChange(BaseActivity.this, paramInt, BaseActivity.this.bluetoothComm);
                if (BaseActivity.this.menu != null) {
                    BaseCommon.refreshMenu(BaseActivity.this);
                }
            }
        });
    }

    protected void onCreate(Bundle paramBundle) {
        this.isDestroyed = false;
        WindowManager localWindowManager = (WindowManager) getSystemService("window");
        SCREEN_WIDTH = localWindowManager.getDefaultDisplay().getWidth();
        SCREEN_HEIGHT = localWindowManager.getDefaultDisplay().getHeight();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(paramBundle);
        initLang();
        setContentView(setContentView());
        this.mContext = this;
        mLocalStack.add(this);
        initData(paramBundle);
        initBluetooth();
        initView();
    }

    protected void onDestroy() {
        dias.remove(this.lodingDialog);
        if (this.lodingDialog != null) {
            this.lodingDialog = null;
        }
        this.isDestroyed = true;
        mLocalStack.remove(this);
        super.onDestroy();
    }

    //发现blu蓝牙设备
    @Override
    public void onDeviceFound(final ArrayList<BluetoothIBridgeDevice> paramArrayList) {
        System.out.println("onDeviceFound------->" + paramArrayList.size());
        this.arrayList = paramArrayList;
        runOnUiThread(new Runnable() {
            public void run() {
                BaseActivity.mLocalStack.peek().dismissDialog();
                if (paramArrayList.size() > 0) {
                    try {
                        BaseCommon.showDeviceDialog(BaseActivity.mLocalStack.peek(), BaseActivity.this.bluetoothComm, paramArrayList);
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                } else {
                    BaseActivity.this.showToast(R.string.not_found_bluetooth);
                }
            }
        });
    }

    //jhl设备信息
    @Override
    public void onDeviceInfo(Map<String, String> paramMap) {
        dismissDialog();
        deviceInfo = paramMap;
        System.out.println("onDeviceInfo------>" + paramMap);
    }

    //jhl刷卡之后的回调信息
    @Override
    public void onReadCardData(Map paramMap) {
        System.out.println("onReadCardData------->");
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry localEntry = (Map.Entry) iterator.next();
            System.out.println(localEntry.getKey() + "------" + localEntry.getValue());
        }
    }


    public void onResult(final int paramInt1, final int paramInt2, final String paramString) {
//        System.out.println("onResult------>" + paramInt1 + "---" + paramInt2 + "---" + paramString);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                switch (paramInt1) {
//                    default:
//                        break;
//                    case 80:
//                        showToast(paramString.toString());
//                        break;
//                    case 18:
//                    case 24:
//                        if (paramInt2 == 0) {
//                            showPasswordInputDialog(ReceivablesActivity.this);
//                            break;
//                        }
//                        showToast("刷卡失败,错误代码:" + Integer.toString(paramInt2));
//                        break;
//                    case 52:
//                        if (paramInt2 == 0) {
//                            showToast("主密钥设置成功");
//                            break;
//                        }
//                        showToast("主密钥设置失败,错误代码:" + Integer.toString(paramInt2));
//                        break;
//                    case 56:
//                        if (paramInt2 == 0) {
//                            showToast("工作密钥设置成功");
//                            break;
//                        }
//                        showToast("工作密钥设置失败,错误代码:" + Integer.toString(paramInt2));
//                        break;
//                    case 55:
//                        if (paramInt2 == 0) {
//                            showToast("MAC:" + paramString.toString());
//                            break;
//                        }
//                        showToast("GetMac失败,错误代码:" + Integer.toString(paramInt2));
//                        break;
//                    case 66:
//                        if (paramInt2 == 0) {
//                            showToast("商户号终端号设置成功");
//                            showToast("正在读取TerNo...");
//                            bluetoothComm.ReadTernumber();
//                            break;
//                        }
//                        showToast("商户号终端号设置失败,错误代码:" + Integer.toString(paramInt2));
//                        break;
//                    case 65:
//                        if (paramInt2 == 0) {
//                            showToast("商户号终端号:" + paramString.toString());
//                            break;
//                        }
//                        showToast("商户号终端号读取失败:" + Integer.toString(paramInt2));
//                }
//            }
//        });
    }

    protected void onResume() {
        super.onResume();
        if ((this.menu != null) && (this.menu.getItem(0) != null)) {
            BaseCommon.refreshMenu(this);
        }
        bluetoothComm = BluetoothCommmanager.getInstance(this, getApplicationContext());
    }

    public void onTimeout() {
        dismissDialog();
        System.out.println("onTimeOut------>");
    }

    public void parseData(String paramString) {
        System.out.println("-------parseData------" + paramString);
    }

    public void pinPad(PinPadEvent paramPinPadEvent) {
        System.out.println("-------pinPad------");
    }

    public String setBackImage() {
        return "";
    }

    public abstract int setContentView();

    public abstract int setTitle();

    public void setTitleTextColor(int paramInt) {
        this.toolbar.setTitleTextColor(paramInt);
    }

    protected void setToolbarTitle(int paramInt) {
        if (this.toolbar != null) {
            this.toolbar.setTitle(paramInt);
        }
    }


    public void showCancelDialog(String paramString) {
        initProgressDialog(paramString, true);
    }

    public void showCancelDialogRes(int paramInt) {
        showCancelDialog(getString(paramInt));
    }

    public void showDialog(String paramString) {
        initProgressDialog(paramString, false);
    }

    public void showDialogRes(int paramInt) {
        showDialog(getString(paramInt));
    }

    public void showToast(int paramInt) {
        showToast(getString(paramInt));
    }

    public void showToast(String paramString) {
        MyToast.show(this, paramString);
    }

    public void startActivity(Class<? extends BaseActivity> paramClass) {
        startActivity(new Intent(this, paramClass));
    }

    public void startToSearchDevice() {
        showDialogRes(R.string.search_bluetooth);
        bluetoothComm.ScanDevice(DEVICE_ADDRESS_FILETER, 5, 0);
    }

    public void startToSearchDevice(boolean paramBoolean) {
        showDialogRes(R.string.search_bluetooth);
        if (paramBoolean) {
            bluetoothHandler.startDiscovery();
        } else {
            this.bluetoothComm.ScanDevice(DEVICE_ADDRESS_FILETER, 5, 0);
        }
    }

    public void startedDiscovery() {
        System.out.println("-------startedDiscovery------");
    }


}
