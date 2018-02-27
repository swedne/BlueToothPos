package com.swed.pos.net;


import java.util.HashMap;
import java.util.Map;


public class BusinessAPI {

    /**
     * 用户登陆
     */
    static final String URL_LOGIN = "GameAuth/login";
    /**
     * 获取跳转链接
     */
    static final String H5_URL = "http://upay.xmktv.net?g=app& m=public&a=getUrl";

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     */
    public static void login(String username,
                             String password,
                             OkHttpManager.HttpCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        OkHttpManager.getInstance().postAsync(URL_LOGIN, params, callback);
    }

    /**
     * 获取跳转的url
     */
    public static void getUrl(OkHttpManager.HttpCallback callback) {
        OkHttpManager.getInstance().postAsync(H5_URL, callback);
    }
}
