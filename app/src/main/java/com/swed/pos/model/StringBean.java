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
        private Integer show_time;

        public Integer getShow_time() {
            return show_time;
        }

        public void setShow_time(Integer show_time) {
            this.show_time = show_time;
        }

        public boolean getIs_show() {
            return is_show;
        }

        private boolean is_show;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
