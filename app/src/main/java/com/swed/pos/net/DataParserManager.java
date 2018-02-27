package com.swed.pos.net;

import com.google.gson.Gson;
import com.swed.pos.model.QueryResult;

/**
 * 数据解析管理类
 * Created by Laozq on 2016/9/29.
 */

public class DataParserManager {

    /**
     * 成功结果编号 1
     */
    private static final int SUCCESS_STATUS_1 = 1;
    /**
     * 成功结果编号 200
     */
    private static final int SUCCESS_STATUS_200 = 200;
    private static final int SUCCESS_STATUS_0 = 0;
    /**
     * 会话过期 401
     */
    public static final int SUCCESS_STATUS_401 = 401;
    /**
     * 系统维护
     */
    public static final int SYSTEM_MAINTANCE_600 = 600;
    /**
     * 账号在其他地方登陆
     */
    public static final int SUCCESS_STATUS_402 = 402;

    public static QueryResult parser(String url, String jsonString, Class<?> aClass) {
        QueryResult result;
        try {
            result = DataParserManager.parserIsSuccess(jsonString, aClass);
        } catch (Exception e) {
            e.printStackTrace();
            result = new QueryResult();
            result.setSuccess(false);
            result.setHasData(false);
            result.setMsg("解析失败");
        }
        return result;
    }

    /**
     * 解析返回的json
     */
    private static QueryResult parserJsonString(String jsonString, Class<?> aClass) throws Exception {
        QueryResult result;
//        try {
        Gson gson = new Gson();
        result = (QueryResult) gson.fromJson(jsonString, aClass);
        result.setSuccess(isSuccess(result));
//        QueryResult result = (QueryResult) JSON.parseObject(jsonString, aClass);
//        result.setSuccess(isSuccess(result));
//        if (result.isSuccess()) {
//            result.setHasData(!TextUtils.isEmpty(result.getData()) && !"[]".equals(result.getData().trim()));
//        }
        return result;
    }

    /**
     * 解析是否操作成功JSON
     */
    private static QueryResult parserIsSuccess(String jsonString, Class<?> aClass) throws Exception {
        QueryResult result = parserJsonString(jsonString, aClass);
        result.setSuccess(isSuccess(result));
        if (result.isSuccess()) {

        } else {

        }
        return result;
    }

    /**
     * 返回请求码是否为成功
     *
     * @return 请求码是否为成功
     */
    private static boolean isSuccess(QueryResult result) {
        return result.getStatus() == SUCCESS_STATUS_200
                || result.getStatus() == SUCCESS_STATUS_1 || result.getStatusCode() == SUCCESS_STATUS_0;
    }
}
