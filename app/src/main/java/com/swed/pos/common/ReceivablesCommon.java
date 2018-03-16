package com.swed.pos.common;

import android.widget.EditText;

import com.jhl.jhlblueconn.BluetoothCommmanager;
import com.mf.mpos.pub.Controler;
import com.swed.pos.BaseActivity;
import com.swed.pos.OtherWebActivity;
import com.swed.pos.model.CardData;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.DialogUtil;
import com.swed.pos.util.MyToast;
import com.swed.pos.util.TimeUtil;

import java.text.ParseException;
import java.util.HashMap;

public class ReceivablesCommon {
    public static final String REQUEST_RATE = "/index.php?g=app&m=Dict&a=getrate";
    public static final String REQUEST_RECEIVABLES = "/index.php?g=app&m=Consume&a=consume";

    public static boolean checkData(BaseActivity paramReceivablesActivity) {
//        String str1 = paramReceivablesActivity.getCurrentType().getStarttime();
//        String str2 = paramReceivablesActivity.getCurrentType().getEndtime();
        String str1 = "00:00";
        String str2 = "00:01";
        try {
            if (!TimeUtil.isCurrentTimeAcrossIntervals(str1, str2)) {
                paramReceivablesActivity.showToast(R.string.time_not_across_intervals);
                return false;
            }
        } catch (ParseException localParseException) {
            paramReceivablesActivity.showToast(R.string.system_error);
            localParseException.printStackTrace();
            return false;
        }
//        if (paramReceivablesActivity.getSourceMoney() <= 0.0F) {
//            paramReceivablesActivity.showToast(R.string.money_must_bigger_zero);
//            return false;
//        }
//        if (TextUtil.isEmpty(paramReceivablesActivity.getAccountName())) {
//            paramReceivablesActivity.showToast(R.string.name_is_empty);
//            return false;
//        }
        return true;
    }

    public static void commitData(CardData paramCardData, BaseActivity paramReceivablesActivity) {
        HashMap localHashMap = new HashMap();
        localHashMap.put("cardid", paramCardData.getCardid());
        localHashMap.put("merchant", paramCardData.getMerchant());
        localHashMap.put("cardholder", paramCardData.getCardholder());
        localHashMap.put("amountrmb", paramCardData.getAmountrmb());
        localHashMap.put("amountother", paramCardData.getAmountother());
        localHashMap.put("exchangerate", paramCardData.getExchangerate());
        localHashMap.put("downgrad", paramCardData.getDowngrad());
        localHashMap.put("encrytrack2len", paramCardData.getEncrytrack2len());
        localHashMap.put("asciipwd", paramCardData.getAsciipwd());
        localHashMap.put("pan", paramCardData.getPan());
        localHashMap.put("encrytrack3len", paramCardData.getEncrytrack3len());
        localHashMap.put("encrytrack2", paramCardData.getEncrytrack2());
        localHashMap.put("expiredate", paramCardData.getExpiredate());
        localHashMap.put("sndata", paramCardData.getSndata());
        localHashMap.put("track55len", paramCardData.getTrack55len());
        localHashMap.put("pinblock", paramCardData.getPinblock());
        localHashMap.put("szentrymode", paramCardData.getSzentrymode());
        localHashMap.put("ctrack3ardid", paramCardData.getCtrack3ardid());
        localHashMap.put("expire", paramCardData.getExpire());
        localHashMap.put("track", paramCardData.getTrack());
        localHashMap.put("card_type", paramCardData.getCard_type());
        localHashMap.put("sn", paramCardData.getSn());
        localHashMap.put("mach_id", paramCardData.getMach_id());
        localHashMap.put("service_code", paramCardData.getService_code());
        localHashMap.put("status", paramCardData.getStatus());
        localHashMap.put("error_code", paramCardData.getError_code());
        localHashMap.put("checksum", paramCardData.getChecksum());
        localHashMap.put("expiretime", paramCardData.getExpiretime());
        localHashMap.put("track3", paramCardData.getTrack3());
        localHashMap.put("amount", paramCardData.getAmount());
        localHashMap.put("track3len", paramCardData.getTrack3len());
        localHashMap.put("track2len", paramCardData.getTrack2len());
        localHashMap.put("track55", paramCardData.getTrack55());
        localHashMap.put("encrytrack3", paramCardData.getEncrytrack3());
        localHashMap.put("panseqno", paramCardData.getPanseqno());
        localHashMap.put("track2", paramCardData.getTrack2());
//        TextUtil.out(localHashMap.toString());
//        paramReceivablesActivity.showDialogRes(2131099679);
//        PostRequestUtil.getInstance(paramReceivablesActivity).postAsyn("/index.php?g=app&m=Consume&a=consume", localHashMap, 1, new PostRequestUtil.OnResultListener() {
//            public void onResult(Exception paramAnonymousException, int paramAnonymousInt, String paramAnonymousString, PostRequestUtil.ResultData paramAnonymousResultData) {
//                this.val$activity.dismissDialog();
//                if (BaseCommon.loginTimeOut(this.val$activity, paramAnonymousResultData)) {
//                    return;
//                }
//                if (paramAnonymousResultData.getSuccessBoolean()) {
//                    this.val$activity.showToast(2131099771);
//                    this.val$activity.finish();
//                    return;
//                }
//                this.val$activity.showToast(this.val$activity.getString(2131099770) + ":" + paramAnonymousResultData.getErrormsg());
//            }
//        });
    }


    public static void showPasswordInputDialog(final OtherWebActivity paramReceivablesActivity) {
        paramReceivablesActivity.pwd = "";
        //jhl密码框
        DialogUtil.showPasswordDialog(paramReceivablesActivity, R.string.place_input_password, 2, new DialogUtil.OnMoneyInputListener() {
            public void onMoneyInput(EditText paramAnonymousEditText) {
                paramReceivablesActivity.pwd = paramAnonymousEditText.getText().toString();
                BluetoothCommmanager.InputPassword(paramAnonymousEditText.getText().toString(), paramAnonymousEditText.getText().toString().length());
            }
        });
    }

    public static void startSwippingCard(OtherWebActivity paramReceivablesActivity, String money, int classify) {
//        if (!checkData(paramReceivablesActivity)) {
//            return;
//        }
//        do {
//            if (!BaseActivity.bOpenDevice) {
//                break;
//            }
//        } while (BaseActivity.bluetoothHandler == null);
        if (classify == paramReceivablesActivity.ZC) {
            if (BaseActivity.bluetoothHandler != null) {
                if (!BaseActivity.bluetoothHandler.isConnected()) {
                    MyToast.show(paramReceivablesActivity, "蓝牙连接设备失败,请断开蓝牙重新连接");
                    return;
                }
            }

        }
//        else if (classify == paramReceivablesActivity.JHL) {
//            if (BaseActivity.bluetoothComm.GetDeviceInfo() == -1) {
//                MyToast.show(paramReceivablesActivity, "蓝牙连接设备失败,请断开蓝牙重新连接");
//                return;
//            }
//
//        }
        else if (classify == paramReceivablesActivity.MF) {
            if (!Controler.posConnected()) {
                MyToast.show(paramReceivablesActivity, "蓝牙连接设备失败,请断开蓝牙重新连接");
                return;
            }

        } else if (classify == paramReceivablesActivity.MFBLACK) {
            if (!Controler.posConnected()) {
                MyToast.show(paramReceivablesActivity, "蓝牙连接设备失败,请断开蓝牙重新连接");
                return;
            }

        }
        paramReceivablesActivity.startSwippingCard(money);
        if (BaseActivity.bluetoothHandler != null) {
            BaseActivity.bluetoothHandler.removeBluetoothListener(paramReceivablesActivity);
            BaseActivity.bluetoothHandler.addBluetoothListener(paramReceivablesActivity);
            paramReceivablesActivity.setSwipCard(true);
        }
//        BaseCommon.clickMenu(paramReceivablesActivity);
    }
}
