package com.swed.pos;

/**
 * Created by Administrator on 2018/2/10.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.imagpay.BluetoothBean;
import com.imagpay.ErrMsg;
import com.imagpay.SwipeHandler;
import com.imagpay.enums.BluetoothType;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.PinPadEvent;
import com.jhl.jhlblueconn.BluetoothCommmanager;
import com.swed.pos.common.BaseCommon;
import com.swed.pos.common.ReceivablesCommon;
import com.swed.pos.model.ConsumerType;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.DialogUtil;
import com.swed.pos.util.MyToast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class SelectTypeActivity extends BaseActivity {
    private boolean swipCard = false;
    private ConsumerType currentType;
    public String pwd = "123456";

    public void connected(BluetoothType paramBluetoothType) {
        type = paramBluetoothType;
        runOnUiThread(new Runnable() {
            public void run() {
                SelectTypeActivity.this.onBlueState(1);
                BaseActivity.settings = BaseActivity.bluetoothHandler.shareSettingsInstance();
                String str = BaseActivity.settings.getSN();
                HashMap localHashMap = new HashMap();
                localHashMap.put("SN", str);
                SelectTypeActivity.this.showToast(R.string.band_bluetooth_success);
                SelectTypeActivity.this.onDeviceInfo(localHashMap);
            }
        });
    }

    public void disconnect() {
        onBlueState(2);
        System.out.println("-------disconnect------");
    }

    public void errMsg(ErrMsg paramErrMsg) {
        MyToast.show(this, "异常：" + paramErrMsg.getMsg());
        System.out.print("异常：" + paramErrMsg.getMsg());
    }

    @Override
    public void returnCardInfo(HashMap hashMap) {

    }

    public void findReader(BluetoothBean paramBluetoothBean) {
        Iterator localIterator = this.beanList.iterator();
        while (localIterator.hasNext()) {
            BluetoothBean localBluetoothBean = (BluetoothBean) localIterator.next();
            if (paramBluetoothBean.getDevice().getAddress().equals(localBluetoothBean.getDevice().getAddress())) {
                return;
            }
        }
        this.beanList.add(paramBluetoothBean);
    }

    public void finishedDiscovery() {
        System.out.println("-------finishedDiscovery------");
        runOnUiThread(new Runnable() {
            public void run() {
                BaseCommon.showDeviceDialog(SelectTypeActivity.this, BaseActivity.bluetoothHandler, SelectTypeActivity.this.beanList);
            }
        });
    }

    public void initData(Bundle paramBundle) {
        currentType = new ConsumerType();
    }

    public void initView() {
        if (bluetoothHandler != null) {
            bluetoothHandler.addBluetoothListener(this);
        }
        findViewById(R.id.type_new).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseCommon.searchDevice(SelectTypeActivity.this, true);
            }
        });
        findViewById(R.id.type_old).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseCommon.searchDevice(SelectTypeActivity.this, false);
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRes(R.string.place_swipping_card);
                ReceivablesCommon.startSwippingCard(SelectTypeActivity.this);
            }
        });
    }

    @Override
    public void onBlueState(int paramInt) {
        if (paramInt == 1) {
            dismissDialog();
            showCancelDialogRes(R.string.connect_success_getsn);
            bOpenDevice = true;
            this.bluetoothComm.GetDeviceInfo();
        }
        super.onBlueState(paramInt);
    }


    //获取jhl的设备信息
    public void onDeviceInfo(final Map<String, String> paramMap) {
        super.onDeviceInfo(paramMap);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                SelectTypeActivity.this.setResult((String) paramMap.get("SN"));
//            }
//        });
    }

    public void onReadCardData(final Map paramMap) {
        runOnUiThread(new Runnable() {
            public void run() {
                ReceivablesCommon.onGetCardInfo(SelectTypeActivity.this, paramMap);
            }
        });
    }

    public void parseData(String paramString) {
        System.out.println("-------parseData------" + paramString);
    }

    public void pinPad(PinPadEvent paramPinPadEvent) {
        System.out.println("-------pinPad------");
    }

    public int setContentView() {
        return R.layout.activity_select_type;
    }

    public void setResult(String paramString) {
        Intent localIntent = new Intent();
        localIntent.putExtra("sn", paramString);
        setResult(-1, localIntent);
        finish();
    }

    public int setTitle() {
        return R.string.choose_dev;
    }

    public void startedDiscovery() {
        System.out.println("-------startedDiscovery------");
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


    //imagpay刷卡回调接口
    @Override
    public void cardDetected(final CardDetected paramCardDetected) {
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
                    DialogUtil.showPasswordDialog(SelectTypeActivity.this, R.string.place_input_password, 2, new DialogUtil.OnMoneyInputListener() {
                        public void onMoneyInput(EditText paramAnonymous2EditText) {
                            String pwd = paramAnonymous2EditText.getText().toString();
                            onReadCardData(hashMap);
                        }
                    });
                }
            });
        }
    }

    public void startSwippingCard() {
        this.swipCard = false;
        showDialogRes(R.string.place_swipping_card);
        //0.01金额
        this.bluetoothComm.MagnCard(15000L, (long) (0.01 * 100.0F), 1);
    }

    public void setSwipCard(boolean paramBoolean) {
        this.swipCard = paramBoolean;
    }


    public ConsumerType getCurrentType() {
        return currentType;
    }
}
