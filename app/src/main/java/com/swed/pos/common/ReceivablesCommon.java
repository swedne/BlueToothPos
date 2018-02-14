package com.swed.pos.common;

import com.swed.pos.BaseActivity;
import com.swed.pos.SelectTypeActivity;
import com.swed.pos.model.CardData;
import com.swed.pos.model.ConsumerType;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.TextUtil;
import com.swed.pos.util.TimeUtil;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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


    public static void onGetCardInfo(SelectTypeActivity paramReceivablesActivity, Map paramMap) {
        HashMap localHashMap = new HashMap();
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry next = (Map.Entry) iterator.next();
            TextUtil.out((String) (next.getKey()) + "------" + (String) (next).getValue());
            localHashMap.put(((Map.Entry) next).getKey(), ((Map.Entry) next).getValue());
        }
        CardData localObject = new CardData();
        ConsumerType currentType = paramReceivablesActivity.getCurrentType();
        localObject.setCardid((String) localHashMap.get("PAN"));
        String money = "0.01";
        localObject.setAmountrmb(money);
        //平台结算金额
        String sourceMoney = "0.01";
        localObject.setAmountother(sourceMoney);
        localObject.setMerchant(paramReceivablesActivity.getCurrentType().getId() + "");
        localObject.setExchangerate(localObject.getExchangerate());
        localObject.setDowngrad((String) localHashMap.get("Downgrad"));
        localObject.setEncrytrack2((String) localHashMap.get("Encrytrack2len"));
        if (TextUtil.isEmpty((String) localHashMap.get("AsciiPwd"))) {
            String psw = paramReceivablesActivity.pwd;
            localObject.setAsciipwd(psw);
            localObject.setPan((String) localHashMap.get("PAN"));
            localObject.setEncrytrack3len((String) localHashMap.get("Encrytrack3len"));
            localObject.setEncrytrack2((String) localHashMap.get("Encrytrack2"));
            localObject.setExpiredate((String) localHashMap.get("ExpireDate"));
//            if (localHashMap.get("SnData") == null) {
////                break label571;
//                return;
//            }
            localObject.setSn((String) localHashMap.get("SnData"));
            localObject.setSndata((String) localHashMap.get("SnData"));
        }
//        for (; ; ) {
//            localObject.setTrack55len((String) localHashMap.get("Track55len"));
//            localObject.setPinblock((String) localHashMap.get("Pinblock"));
//            localObject.setSzentrymode((String) localHashMap.get("SzEntryMode"));
//            localObject.setTrack3((String) localHashMap.get("Track3"));
//            localObject.setCard_type((String) localHashMap.get("CardType"));
//            localObject.setAmount((String) localHashMap.get("Amount"));
//            localObject.setTrack2len((String) localHashMap.get("Track2len"));
//            localObject.setTrack3len((String) localHashMap.get("Track3len"));
//            localObject.setTrack55((String) localHashMap.get("Track55"));
//            localObject.setEncrytrack3((String) localHashMap.get("Encrytrack3"));
//            localObject.setPanseqno((String) localHashMap.get("PanSeqNo"));
//            localObject.setTrack2((String) localHashMap.get("Track2"));
//            commitData((CardData) localObject, paramReceivablesActivity);
//            return;
//            paramMap = (String) localHashMap.get("AsciiPwd");
//            break;
//            ((CardData) localObject).setSn((String) BaseActivity.deviceInfo.get("SN"));
//            ((CardData) localObject).setSndata((String) BaseActivity.deviceInfo.get("SN"));
//        }
    }

//    public static void setSourceMoneyChange(ReceivablesActivity paramReceivablesActivity) {
//        paramReceivablesActivity.getSourceMoneyEt().addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable paramAnonymousEditable) {
//            }
//
//            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
//            }
//
//            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
//                double d1 = this.val$activity.getSourceMoney();
//                double d2 = this.val$activity.getCurrentType().getExchangerateFloat();
//                this.val$activity.getEndMoneyEt().setText(TextUtil.keepTwoDecimal(d1 * d2));
//            }
//        });
//    }

//    public static void showPasswordInputDialog(ReceivablesActivity paramReceivablesActivity) {
//        paramReceivablesActivity.pwd = "";
//        DialogUtil.showPasswordDialog(paramReceivablesActivity, 2131099737, 2, new DialogUtil.OnMoneyInputListener() {
//            public void onMoneyInput(EditText paramAnonymousEditText) {
//                this.val$activity.pwd = paramAnonymousEditText.getText().toString();
//                BluetoothCommmanager.InputPassword(paramAnonymousEditText.getText().toString(), paramAnonymousEditText.getText().toString().length());
//            }
//        });
//    }

    public static void startSwippingCard(SelectTypeActivity paramReceivablesActivity) {
//        if (!checkData(paramReceivablesActivity)) {
//            return;
//        }
        do {
            if (!BaseActivity.bOpenDevice) {
                break;
            }
            paramReceivablesActivity.startSwippingCard();
        } while (BaseActivity.bluetoothHandler == null);
        BaseActivity.bluetoothHandler.removeBluetoothListener(paramReceivablesActivity);
        BaseActivity.bluetoothHandler.addBluetoothListener(paramReceivablesActivity);
        paramReceivablesActivity.setSwipCard(true);
        BaseCommon.clickMenu(paramReceivablesActivity);
    }
}
