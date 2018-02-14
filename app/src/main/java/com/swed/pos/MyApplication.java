package com.swed.pos;

import android.app.Application;

import com.imagpay.BluetoothBean;
import com.imagpay.BluetoothHandler;
import com.imagpay.BluetoothListener;
import com.imagpay.ErrMsg;
import com.imagpay.enums.BluetoothType;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.PinPadEvent;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/2/9.
 */

public class MyApplication extends Application implements BluetoothListener {
    public BluetoothHandler bluetoothHandler;

    public void cardDetected(CardDetected paramCardDetected) {
    }

    public void connected(BluetoothType paramBluetoothType) {
    }

    public void disconnect() {
    }

    public void errMsg(ErrMsg paramErrMsg) {
    }

    @Override
    public void returnCardInfo(HashMap hashMap) {

    }

    public void findReader(BluetoothBean paramBluetoothBean) {
    }

    public void finishedDiscovery() {
    }

    public void onCreate() {
        super.onCreate();
        if (this.bluetoothHandler != null) {
            this.bluetoothHandler = new BluetoothHandler(getApplicationContext());
            this.bluetoothHandler.addBluetoothListener(this);
        }
    }

    public void parseData(String paramString) {
    }

    public void pinPad(PinPadEvent paramPinPadEvent) {
    }

    public void startedDiscovery() {
    }
}

