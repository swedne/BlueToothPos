package com.swed.pos.model;

/**
 * Created by Administrator on 2018/2/26.
 */

public class StringBean extends QueryResult {

    private StringData data;

    public StringData getDataMsg() {
        return data;
    }

    public class StringData {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
