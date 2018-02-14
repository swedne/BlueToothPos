package com.swed.pos.net;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.swed.pos.model.QueryResult;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OKHttp请求类
 */

public class OkHttpManager {
    public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TYPE_PIC = MediaType.parse("image/jpeg");

    /**
     * 请求超时，秒
     */
    private static final int TIME_OUT_REQUEST = 10;
    private static final int TIME_OUT_WRITE = 20;

    /**
     *
     */
    private static final int TIME_OUT_PIC = 120;

    /**
     * OKHttp请求类 实例
     */
    private static OkHttpManager mInstance;
    /**
     * OkHttpClient 实例
     */
    private OkHttpClient mClient;

    private static Handler mHandler;

    private static final String TAG = "OkHttpManager";

    /**
     * 构造方法
     */
    private OkHttpManager() {

        mClient = new OkHttpClient();

        // 在这里直接设置连接超时.读取超时，写入超时
        mClient.newBuilder().connectTimeout(TIME_OUT_REQUEST, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_REQUEST, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_WRITE, TimeUnit.SECONDS);

        mHandler = new Handler(Looper.getMainLooper());

    }

    /**
     * 获取实例
     *
     * @return 实例
     */
    public static OkHttpManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpManager.class) {
                mInstance = new OkHttpManager();
            }
        }
        return mInstance;
    }

    /**
     * GET方式请求方式，同步
     *
     * @param url 链接
     * @return Json
     */
    public String getSync(String url) {
        return getResponseString(getSyncResponse(url));
    }

    /**
     * GET方式请求方式，同步
     *
     * @param url 链接
     * @return Response类
     */
    public Response getSyncResponse(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            printUrl(url);
            //同步请求返回的是response对象
            response = mClient.newCall(request).execute();
            printSuccess(url, getResponseString(response));
        } catch (IOException e) {
            printFail(url, e);
        }
        return response;
    }

    /**
     * GET方式请求方式，异步
     *
     * @param url      链接
     * @param callback 回调
     */
    public void getAsync(String url,
                         Map<String, String> param,
                         HttpCallback callback) {
        getAsync(url, param, 0, callback);
    }

    /**
     * GET方式请求方式，异步
     *
     * @param url      链接
     * @param params   请求参数
     * @param timeOut  超时
     * @param callback 回调
     */
    public void getAsync(String url,
                         Map<String, String> params,
                         int timeOut,
                         final HttpCallback callback) {
        final Request request = new Request.Builder()
                .url(url + "?" + turnParamsToGet(params))
                .build();
        printUrl(url + "?" + turnParamsToGet(params));
        if (timeOut != 0) {
            new OkHttpClient().newBuilder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .build()
                    .newCall(request).enqueue(callback);
        } else {
            mClient.newCall(request).enqueue(callback);
        }
    }

    /**
     * 把参数转化为字符串
     *
     * @param map 参数
     * @return 字符串格式的参数
     */
    public static String turnParamsToGet(Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        if (map == null) {
            return "";
        }
        for (String key : map.keySet()) {
            stringBuilder.append(key)
                    .append("=")
                    .append(map.get(key))
                    .append("&");
        }
        if (stringBuilder.length() != 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * POST方式请求方式，同步
     *
     * @param url    链接
     * @param params post的参数
     * @return Json
     */
    public String postSync(String url,
                           Map<String, String> params) {
        return getResponseString(postSyncResponse(url, params, null));
    }

    /**
     * POST方式请求方式，同步
     *
     * @param url     链接
     * @param params  post的参数
     * @param headers 请求头参数
     * @return Json
     */
    public String postSync(String url,
                           Map<String, String> params,
                           Map<String, String> headers) {
        return getResponseString(postSyncResponse(url, params, headers));
    }


    /**
     * POST方式请求方式，同步
     *
     * @param url    链接
     * @param params post的参数
     * @return Response类
     */
    public Response postSyncResponse(String url,
                                     Map<String, String> params) {
        return postSyncResponse(url, params, null);
    }

    /**
     * POST方式请求方式，同步
     *
     * @param url     链接
     * @param params  post的参数
     * @param headers 请求头参数
     * @return Response类
     */
    public Response postSyncResponse(String url,
                                     Map<String, String> params,
                                     Map<String, String> headers) {
        String paramsString = turnParamsToString(params);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .tag(paramsString)
                .post(turn2Body(params));
        addHeader(builder, headers);
        Request request = builder.build();
        Response response = null;
        try {
            printUrl(url);
            response = mClient.newCall(request).execute();
            printSuccess(url, getResponseString(response));
        } catch (IOException e) {
            printFail(url, e);
        }
        return response;
    }

    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param callback 回调
     */
    public void postAsync(String url,
                          HttpCallback callback) {
        postAsync(url, null, callback);
    }

    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param params   post的参数
     * @param callback 回调
     */
    public void postAsync(String url,
                          Map<String, String> params,
                          HttpCallback callback) {
        postAsync(url, params, null, callback);
    }

    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param params   post的参数
     * @param headers  请求头参数
     * @param callback 回调
     */
    public void postAsync(String url,
                          Map<String, String> params,
                          Map<String, String> headers,
                          HttpCallback callback) {
        postAsync(url, params, headers, 0, callback);

    }


    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param timeOut  超时时间，秒为单位。为0时使用默认e
     * @param callback 回调
     */
    public void postAsync(String url,
                          int timeOut,
                          boolean isGame,
                          HttpCallback callback) {
        postAsync(url, null, timeOut, callback);

    }

    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param params   post的参数
     * @param timeOut  超时时间，秒为单位。为0时使用默认
     * @param callback 回调
     */
    public void postAsync(String url,
                          Map<String, String> params,
                          int timeOut,
                          HttpCallback callback) {
        postAsync(url, params, null, timeOut, callback);

    }

    /**
     * POST方式请求方式，异步
     *
     * @param url      链接
     * @param params   post的参数
     * @param headers  请求头参数
     * @param timeOut  超时时间，秒为单位。为0时使用默认
     * @param callback 回调
     */
    public void postAsync(String url,
                          Map<String, String> params,
                          Map<String, String> headers,
                          int timeOut,
                          HttpCallback callback) {
        String urlString = url;
        Request.Builder builder = new Request.Builder()
                .url(urlString)
                .tag(turnParamsToString(params))
                .post(turn2Body(params));

        addHeader(builder, headers);
        Request request = builder.build();
        //打印连接
        printUrl(urlString, params);
        if (timeOut != 0) {
            new OkHttpClient().newBuilder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .build()
                    .newCall(request).enqueue(callback);
        } else {
            mClient.newCall(request).enqueue(callback);
        }
    }


    /**
     * 上传文件
     *
     * @param url      链接
     * @param params   post的参数
     * @param callback 回调
     */
    public void upLoadFile(String url,
                           HashMap<String, Object> params,
                           HttpCallback callback) {
        String urlString = url;

        Map<String, String> tmpMap = new HashMap<>();
        for (String key : params.keySet()) {
            Object obj = params.get(key);
            if (obj instanceof String) tmpMap.put(key, (String) obj);
        }
//        tmpMap = sign(tmpMap);
        params.putAll(tmpMap);

        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        //追加参数
        for (String key : params.keySet()) {
            Object object = params.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(TYPE_PIC, file));
            }
        }
        final Request request = new Request.Builder()
                .url(urlString)
                .tag(turnParamsToString(tmpMap))
                .post(builder.build())
                .build();

        new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT_PIC, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_PIC, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT_PIC, TimeUnit.SECONDS)
                .build()
                .newCall(request).enqueue(callback);
    }


    /**
     * 签名
     *
     * @param map 参数
     * @return 带签名的参数列表
     */
//    public static Map<String, String> sign(Map<String, String> map) {
//        if (map == null) map = new HashMap<>();
//        try {
//
//            for (String key : map.keySet()) {
//                String value = map.get(key);
//                // 当value值为空时则移除该key
//                if (value == null) map.remove(key);
//            }
//
//            //zrz 必要的传入参数
//            map.put("t", "" + System.currentTimeMillis() / 1000);
//            map.put("pkg", "global");
//            map.put("platform", "3");
//            map.put("channel_code", StringUtil.getAppMetaData());
//            if (MyApplication.getInstance().isLogin()) {
//                map.put("userToken", MyApplication.getInstance().getToken());
//            }
//
//            String sign = turnParamsToString(sort(map));
//            sign = HmacSHA1Encryption.hmacSHA1Encrypt(sign, Constant.KEY);
//            map.put("sign", sign);
//
//        } catch (Exception ex) {
//            LogUtil.e("URL签名失败", ex);
//        }
//        return map;
//    }


    /**
     * 把参数转化为字符串
     *
     * @param map 参数
     * @return 字符串格式的参数
     */
    public static String turnParamsToString(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject(map);
        String result = jsonObject.toString();

//        StringBuilder stringBuilder = new StringBuilder();
//        for (String key : map.keySet()) {
//            stringBuilder.append(key)
//                    .append("=")
//                    .append(map.get(key))
//                    .append("&");
//        }
//        if (stringBuilder.length() != 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return result;
    }

    /**
     * 转格式
     */
    private static RequestBody turn2Body(Map<String, String> params) {
        JSONObject jsonObject = new JSONObject(params);
        String result = jsonObject.toString();

//        FormBody.Builder builder = new FormBody.Builder();
//        for (String key : params.keySet()) {
//            builder.add(key, params.get(key));
//        }
        return RequestBody.create(TYPE_JSON, result);
    }

    /**
     * 设置请求头
     *
     * @param params 请求头的参数
     * @return 请求头
     */
    private Headers toHeaders(Map<String, String> params) {
        okhttp3.Headers.Builder builder = new okhttp3.Headers.Builder();
        if (params != null) {
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                builder.add(key, params.get(key));
            }
        }
        Headers headers = builder.build();
        return headers;
    }

    /**
     * 添加头清求
     *
     * @param builder 头构造
     * @param headers 头参数
     */
    private void addHeader(Request.Builder builder,
                           Map<String, String> headers) {
        if (headers == null) {

        } else {
            builder.headers(toHeaders(headers));
        }
    }

    /**
     * 对参数排序
     *
     * @param map 参数
     * @return builder 排序后的参数
     */
    private static Map<String, String> sort(Map<String, String> map) {
        return new TreeMap<>(map);
    }

    /**
     * 重试次数
     *
     * @param response 响应
     * @return 次数
     */
    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }


    /**
     * 打印请求信息日志
     *
     * @param url 链接
     */
    private void printUrl(String url) {
        Log.e(TAG, "GET请求: url = " + url);
    }

    /**
     * 打印请求信息日志
     *
     * @param url 链接
     * @param map 表单
     */
    private void printUrl(String url, Map<String, String> map) {
        Log.e(TAG, "POST请求: url = " + url + "?" + turnParamsToString(map));

    }

    /**
     * 打印请求成功日志
     *
     * @param url      请求
     * @param response 返回内容
     */
    private static void printSuccess(String url, String response) {
        Log.e(TAG, "请求成功，url = " + url);
    }

    /**
     * 打印请求成功日志
     *
     * @param call     请求
     * @param response 返回内容
     */
    private static void printSuccess(Call call, String response) {
        Log.e(TAG, "请求成功，url = " + call.request().url() + "?" + call.request().tag());
        Log.e(TAG, "请求返回结果: " + response);
    }

    /**
     * 打印请求成功日志
     *
     * @param url 请求
     * @param e   异常
     */
    private static void printFail(String url, Exception e) {
        Log.e(TAG, "请求失败，url = " + url
                + "\nException = " + e.toString());

    }

    /**
     * 打印请求成功日志
     *
     * @param call 请求s
     * @param e    异常
     */
    private static void printFail(Call call, Exception e) {
        Log.e(TAG, "请求失败，url = " + call.request().url() + "?" + call.request().tag()
                + "\nException = " + e.toString());

    }

    /**
     * 格式化response内容
     *
     * @param response 回调内容
     * @return 格式化后回调内容
     */
    private static String getResponseString(Response response) {
        try {
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, "格式化response内容出错," + e.toString());
        }
        return "";
    }


    public abstract static class HttpCallback implements Callback {
        private Class<?> aClass;
        private Activity mActivity;

        public HttpCallback(Class<?> aClass) {
            this.aClass = aClass;
        }

        public HttpCallback(Class<?> aClass, Activity activity) {
            this.aClass = aClass;
            this.mActivity = activity;
        }

        @Override
        public void onFailure(final Call call, final IOException e) {
            printFail(call, e);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailure(e.toString());
                    onFinish();
                    onNetError();
                }
            });
        }

        @Override
        public void onResponse(final Call call, final Response response) {
            final String string = getResponseString(response);
            printSuccess(call, string);
            //将请求回来的字符串解析成实体类
            final QueryResult result = DataParserManager.parser(call.request().url().toString(), string, aClass);
            onBackgroundResponse(result);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (result.isSuccess()) {
                        onResponse(string);
                        onResponse(result);
                    } else {
                        onFailure(result.getMsg());
                        onFailure(result);
                    }
                    onFinish();
                }
            });
        }


        /**
         * 请求成功回调，子线程
         *
         * @param result 解析后的结果
         */
        public void onBackgroundResponse(QueryResult result) {
        }

        /**
         * 请求成功回调
         *
         * @param result 解析后的结果
         */
        public abstract void onResponse(QueryResult result);


        /**
         * 请求成功回调
         *
         * @param returnString 返回的json
         */
        public void onResponse(String returnString) {
        }


        /**
         * 请求失败回调
         *
         * @param error 异常
         */
        public abstract void onFailure(String error);

        public void onFailure(QueryResult result) {

        }

        /**
         * 请求完成回调
         */
        public void onFinish() {
        }

        /**
         * 请求完成回调
         */
        public void onNetError() {
        }

    }

}
