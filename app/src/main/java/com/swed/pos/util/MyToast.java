package com.swed.pos.util;

/**
 * Created by Administrator on 2018/2/10.
 */

import android.content.Context;
import android.widget.Toast;

public class MyToast {
    private static Toast toast;
    private static Toast toastcenter;

    public static void show(Context paramContext, String paramString) {
        if (paramString.trim().equals("")) {
            return;
        }
        try {
            if (toast == null) {
                toast = Toast.makeText(paramContext, paramString, Toast.LENGTH_LONG);
            }
            toast.setText(paramString);
            toast.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void showCenter(Context paramContext, String paramString) {
        if (paramString.trim().equals("")) {
            return;
        }
        try {
            if (toastcenter == null) {
                toastcenter = Toast.makeText(paramContext, paramString, Toast.LENGTH_SHORT);
                toastcenter.setGravity(17, 0, -50);
            }
            toastcenter.setText(paramString);
            toastcenter.show();
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
