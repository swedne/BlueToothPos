package com.swed.pos.common;

/**
 * Created by Administrator on 2018/2/10.
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.imagpay.BluetoothBean;
import com.imagpay.BluetoothHandler;
import com.jhl.bluetooth.ibridge.BluetoothIBridgeDevice;
import com.jhl.jhlblueconn.BluetoothCommmanager;
import com.mf.mpos.pub.Controler;
import com.swed.pos.BaseActivity;
import com.swed.pos.SelectTypeActivity;
import com.swed.pos.myapplication.R;
import com.swed.pos.util.DialogUtil;
import com.swed.pos.util.MyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BaseCommon {
    public static final int REQUEST_CONNECT = 100;
    private static ArrayList<BluetoothDevice> devs = new ArrayList<BluetoothDevice>();


    public static void clickMenu(final BaseActivity paramBaseActivity) {
        paramBaseActivity.isGetSn = false;
        if (paramBaseActivity.bOpenDevice) {
            DialogUtil.showTipsDialog(paramBaseActivity, paramBaseActivity.getString(R.string.is_break_connect), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
//                    paramBaseActivity.showCancelDialogRes(R.string.negative);
                    paramBaseActivity.disconnectBluetooth();
                    paramBaseActivity.startActivityForResult(new Intent(paramBaseActivity, SelectTypeActivity.class), 100);
                }
            });
            return;
        }
        if (BaseActivity.bluetoothHandler != null) {
            BaseActivity.bluetoothHandler.removeBluetoothListener(paramBaseActivity);
        }
    }


    public static void getSN(BaseActivity paramBaseActivity) {
        paramBaseActivity.isGetSn = true;
        if (BaseActivity.deviceInfo != null) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.onDeviceInfo(BaseActivity.deviceInfo);
            return;
        }
        if (BaseActivity.bluetoothHandler != null) {
            BaseActivity.bluetoothHandler.removeBluetoothListener(paramBaseActivity);
        }
        paramBaseActivity.startActivityForResult(new Intent(paramBaseActivity, SelectTypeActivity.class), 100);
    }


    public static boolean onBluetoothStatusChange(BaseActivity paramBaseActivity, int paramInt, BluetoothCommmanager paramBluetoothCommmanager) {
        BaseActivity.deviceInfo = null;
        if (paramInt == -1) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showToast(R.string.not_found_bluetooth);
            return false;
        }
        if (paramInt == 0) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showToast(R.string.connect_bluetooth_error);
            return false;
        }
        if (paramInt == 2) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showToast(R.string.disconnect_bluetooth);
            return false;
        }
        if (paramInt == 1) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showCancelDialogRes(R.string.connect_success_getsn);
            paramBluetoothCommmanager.GetDeviceInfo();
            return true;
        }
        if (paramInt == 3) {
            paramBaseActivity.dismissDialog();
            return false;
        }
        if (paramInt == 4) {
            paramBaseActivity.dismissDialog();
            return false;
        }
        if (paramInt == 5) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showCancelDialogRes(R.string.banding_bluetooth);
            return false;
        }
        if (paramInt == 6) {
            paramBaseActivity.dismissDialog();
            paramBaseActivity.showToast(R.string.band_bluetooth_success);
            return false;
        }
        if (paramInt == 7) {
            paramBaseActivity.dismissDialog();
            System.out.println("绑定蓝牙失败");
            return false;
        }
        paramBaseActivity.dismissDialog();
        return false;
    }

    public static void refreshMenu(BaseActivity paramBaseActivity) {
        if (BaseActivity.bOpenDevice) {
            paramBaseActivity.getMenu().getItem(0).setIcon(R.mipmap.ic_launcher);
            return;
        }
        paramBaseActivity.getMenu().getItem(0).setIcon(R.mipmap.ic_launcher);
    }

    public static void searchDevice(final BaseActivity paramBaseActivity, boolean paramBoolean) {
        if (paramBoolean) {
            //iMagPay
            DialogUtil.showTipsDialog(paramBaseActivity, paramBaseActivity.getString(R.string.is_connect), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramBaseActivity.startToSearchDevice(true);
//                    //寻找设备的loading
//                    paramBaseActivity.showDialogRes(R.string.search_bluetooth);
                }
            });
        } else {
            //jhlblue
            DialogUtil.showTipsDialog(paramBaseActivity, paramBaseActivity.getString(R.string.is_connect), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramBaseActivity.startToSearchDevice();
//                    paramBaseActivity.showDialogRes(R.string.search_bluetooth);
                }
            });
        }
    }

    public static void searchMfDevice(final BaseActivity paramBaseActivity, final BluetoothAdapter adapter, final Handler handler) {
        DialogUtil.showTipsDialog(paramBaseActivity, paramBaseActivity.getString(R.string.is_connect), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                searchMfDevice2(paramBaseActivity, adapter, handler);
            }
        });
    }

    public static void showDeviceDialog(final BaseActivity paramBaseActivity, final BluetoothHandler paramBluetoothHandler, final List<BluetoothBean> paramList) {
        if (paramBaseActivity.isDestroyed()) {
            return;
        }
        if (paramList.size() < 1) {
            MyToast.show(paramBaseActivity, paramBaseActivity.getResources().getString(R.string.unfind_device));
            return;
        }
        paramBaseActivity.dismissDialog();
        final String[] arrayOfString = new String[paramList.size()];
        int i = 0;
        while (i < paramList.size()) {
            arrayOfString[i] = ((paramList.get(i)).getDevice().getName() + "--" + (paramList.get(i)).getType());
            i += 1;
        }
        new AlertDialog.Builder(paramBaseActivity).setTitle(R.string.place_switch_device).setIcon(R.mipmap.ic_launcher).setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, final int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
                paramBaseActivity.disconnectBluetooth();
                paramBaseActivity.showCancelDialog(paramBaseActivity.getString(R.string.connecting) + ":" + arrayOfString[paramAnonymousInt]);
                paramBaseActivity.showToast(paramBaseActivity.getString(R.string.connecting) + ":" + arrayOfString[paramAnonymousInt]);
                // 传入bean对象执行蓝牙连接操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try { // 线程中执行连接操作请先调用Looper.prepare();
                            Looper.prepare();
                            paramBluetoothHandler.connect(paramList.get(paramAnonymousInt));
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (paramBluetoothHandler.isConnected()) {
                                Toast.makeText(paramBaseActivity, "该设备已链接过,请断开蓝牙重新连接", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }).start();

            }
        }).setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        }).create().show();
    }

    public static void showDeviceDialog(final BaseActivity paramBaseActivity, final BluetoothCommmanager paramBluetoothCommmanager, final ArrayList<BluetoothIBridgeDevice> paramArrayList) {
        if (paramBaseActivity.isDestroyed()) {
            return;
        }
        paramBaseActivity.dismissDialog();
        String[] arrayOfString = new String[paramArrayList.size()];
        int i = 0;
        while (i < paramArrayList.size()) {
            arrayOfString[i] = paramArrayList.get(i).getDeviceName();
            i += 1;
        }
        new AlertDialog.Builder(paramBaseActivity).setTitle(R.string.place_switch_device).setIcon(R.mipmap.ic_launcher).setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
                paramBaseActivity.disconnectBluetooth();
                paramBaseActivity.showCancelDialog(paramBaseActivity.getString(R.string.connecting) + ":" + (paramArrayList.get(paramAnonymousInt)).getDeviceName());
                paramBaseActivity.showToast(paramBaseActivity.getString(R.string.connecting) + ":" + (paramArrayList.get(paramAnonymousInt)).getDeviceName());
                paramBluetoothCommmanager.ConnectDevice((paramArrayList.get(paramAnonymousInt)).getDeviceAddress());
            }
        }).setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        }).create().show();
    }

    public static void showMfDeviceDialog(final BaseActivity paramBaseActivity, final ArrayList<BluetoothDevice> paramList, final Handler handler) {
        if (paramBaseActivity.isDestroyed()) {
            return;
        }
        if (paramList.size() < 1) {
            MyToast.show(paramBaseActivity, paramBaseActivity.getResources().getString(R.string.unfind_device));
            return;
        }
        paramBaseActivity.dismissDialog();
        final String[] arrayOfString = new String[paramList.size()];
        int i = 0;
        while (i < paramList.size()) {
            arrayOfString[i] = ((paramList.get(i)).getName());
            i += 1;
        }
        new AlertDialog.Builder(paramBaseActivity).setTitle(R.string.place_switch_device).setIcon(R.mipmap.ic_launcher).setSingleChoiceItems(arrayOfString, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, final int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
                paramBaseActivity.disconnectBluetooth();
                paramBaseActivity.showCancelDialog(paramBaseActivity.getString(R.string.connecting) + ":" + arrayOfString[paramAnonymousInt]);
                paramBaseActivity.showToast(paramBaseActivity.getString(R.string.connecting) + ":" + arrayOfString[paramAnonymousInt]);
                // 开启一个新线程，在新线程里执行连接蓝牙这个有可能耗时的操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //断开原来的连接
                        if (Controler.posConnected()) {
                            Controler.disconnectPos();
                        }
                        //连接
                        String mac = paramList.get(paramAnonymousInt).getAddress();
                        Controler.connectPos(mac);
                        // 等待完毕后发送消息关闭进度框
                        handler.sendEmptyMessage(8);
                    }
                }).start();

//                // 传入bean对象执行蓝牙连接操作
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try { // 线程中执行连接操作请先调用Looper.prepare();
//                            Looper.prepare();
//                            paramBluetoothHandler.connect(paramList.get(paramAnonymousInt));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            if (paramBluetoothHandler.isConnected()) {
//                                Toast.makeText(paramBaseActivity, "该设备已链接过,请断开蓝牙重新连接", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }).start();

            }
        }).setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.cancel();
            }
        }).create().show();
    }


    //刷新已匹配到的蓝牙列表界面
    protected static void searchMfDevice2(final BaseActivity activity, BluetoothAdapter btAdapter, final Handler mHandler) {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        devs.clear();
        for (BluetoothDevice device : pairedDevices) {
            devs.add(device);
        }
        activity.showDialogRes(R.string.search_bluetooth);
        Controler.startSearchDev(new Controler.IonSearchDev() {
            @Override
            public void onSearchDev(BluetoothDevice btDevice) {
                BluetoothDevice bt = btDevice;
                if (!devs.contains(btDevice)) {
                    devs.add(btDevice);
                }
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Controler.stopSearchDev();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        BaseActivity.mLocalStack.peek().dismissDialog();
                        if (devs.size() > 0) {
                            try {
                                BaseCommon.showMfDeviceDialog(BaseActivity.mLocalStack.peek(), devs, mHandler);
                            } catch (Exception localException) {
                                localException.printStackTrace();
                            }
                        } else {
                            activity.showToast(R.string.not_found_bluetooth);
                        }
                    }
                });

            }
        }, 10000);


    }
}
