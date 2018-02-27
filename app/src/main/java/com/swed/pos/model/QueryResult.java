package com.swed.pos.model;


/**
 * 请求返回结果
 *
 * @author Administrator
 */
public class QueryResult {
    /**
     * 返回提示信息
     */
    private String msg;
    private long statusCode;
    private long status;
    private long time;
    /**
     * 查询结果
     */
    private Object object;
    /**
     * 是否成功
     */
    private boolean isSuccess;
    /**
     * 是否有数据
     */
    private boolean hasData;

    public boolean hasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }


    public QueryResult() {
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
