package com.swed.pos.util;

/**
 * Created by Administrator on 2018/2/10.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class ShareManager {
    public static final String ACCOUNT = "account";
    public static final String CHINESE = "ch";
    public static final String ENGLISH = "en";
    private static final String IP_ADDRESS = "ipAddress";
    public static final String ISREMEMBER = "remember";
    private static final String Language = "language";
    public static final String PASSWORD = "password";
    private static final String SHARE_NAME = "pos_base";
    public static final String TOKEN = "accessToken";
    private static ShareManager shareManager;
    SharedPreferences sp;

    private ShareManager(Context paramContext) {
        this.sp = paramContext.getSharedPreferences("pos_base", 0);
    }

    public static ShareManager getInstance(Context paramContext) {
        if (shareManager == null) {
            shareManager = new ShareManager(paramContext);
        }
        return shareManager;
    }

    public boolean getBoolean(String paramString, boolean paramBoolean) {
        return this.sp.getBoolean(paramString, paramBoolean);
    }

    public String getIpAddress(String paramString) {
        return getString("ipAddress", paramString);
    }

    public String getLanguage() {
        return getString("language", "ch");
    }

    public String getString(String paramString1, String paramString2) {
        return this.sp.getString(paramString1, paramString2);
    }

    public boolean put(String paramString1, String paramString2) {
        SharedPreferences.Editor localEditor = this.sp.edit();
        localEditor.putString(paramString1, paramString2);
        return localEditor.commit();
    }

    public boolean put(String paramString, boolean paramBoolean) {
        SharedPreferences.Editor localEditor = this.sp.edit();
        localEditor.putBoolean(paramString, paramBoolean);
        return localEditor.commit();
    }

    public void setIpAddress(String paramString) {
        put("ipAddress", paramString);
    }

    public void setLanguage(String paramString) {
        put("language", paramString);
    }
}

