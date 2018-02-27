package com.swed.pos.util;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import com.swed.pos.myapplication.R;


public class DialogUtil {

    private static EditText et;
    private static OnMoneyInputListener listener;

    public static void showIpAddressDialog(final Context paramContext, final String paramString1, String paramString2, final DialogInterface.OnClickListener paramOnClickListener) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(paramString1);
        et = new EditText(paramContext);
        localBuilder.setView(et);
        et.setText(paramString2);
        localBuilder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                ShareManager.getInstance(paramContext).setIpAddress(et.getText().toString());
                paramOnClickListener.onClick(paramAnonymousDialogInterface, paramAnonymousInt);
            }
        });
        localBuilder.setNegativeButton(R.string.positive, null);
        localBuilder.setNeutralButton(R.string.reset, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramOnClickListener.onClick(paramAnonymousDialogInterface, paramAnonymousInt);
            }
        });
        localBuilder.show();
    }

    public static void showPasswordDialog(final Context paramContext, int paramInt1, int paramInt2, final OnMoneyInputListener paramOnMoneyInputListener) {
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(paramInt1);
        et = new EditText(paramContext);
        et.setInputType(paramInt2);
        et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        localBuilder.setView(et);
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                if (paramOnMoneyInputListener != null) {
                    paramOnMoneyInputListener.onMoneyInput(et);
                }
                paramAnonymousDialogInterface.dismiss();
            }
        });
        localBuilder.setNegativeButton(R.string.negative, null);
        localBuilder.show();
    }

    public static void showTipsDialog(Context paramContext, String paramString, DialogInterface.OnClickListener paramOnClickListener) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(R.string.tips);
        localBuilder.setMessage(paramString);
        localBuilder.setPositiveButton(R.string.positive, paramOnClickListener);
        localBuilder.setNegativeButton(R.string.negative, null);
        localBuilder.show();
    }

    /**
     * @param paramContext
     * @param paramString
     * @param paramOnClickListener1
     * @param paramOnClickListener2
     */
    public static void showTipsDialog(Context paramContext, String paramString, DialogInterface.OnClickListener paramOnClickListener1, DialogInterface.OnClickListener paramOnClickListener2) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
        localBuilder.setTitle(R.string.tips);
        localBuilder.setMessage(paramString);
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton(R.string.positive, paramOnClickListener1);
        localBuilder.setNegativeButton(R.string.negative, paramOnClickListener2);
        localBuilder.show();
    }

    public static abstract interface OnMoneyInputListener {
        public abstract void onMoneyInput(EditText paramEditText);
    }

    public void setMoneyInputListener(OnMoneyInputListener listener) {
        this.listener = listener;
    }
}

