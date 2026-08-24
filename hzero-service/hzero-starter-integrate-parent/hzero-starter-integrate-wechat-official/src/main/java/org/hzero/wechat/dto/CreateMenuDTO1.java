package org.hzero.wechat.dto;

import java.util.List;

public class CreateMenuDTO1 {

    private List<ButtonBean> button;

    public List<ButtonBean> getButton() {
        return button;
    }

    public void setButton(List<ButtonBean> button) {
        this.button = button;
    }

    public static class ButtonBean {
        /**
         * type : click
         * name : 今日歌曲
         * key : V1001_TODAY_MUSIC
         * sub_button : [{"type":"view","name":"搜索","url":"http://www.soso.com/"},{"type":"miniprogram","name":"wxa","url":"http://mp.weixin.qq.com","appid":"wx286b93c14bbf93aa","pagepath":"pages/lunar/index"},{"type":"click","name":"赞一下我们","key":"V1001_GOOD"}]
         */

        private String type;
        private String name;
        private String key;
        private List<SubButtonBean> sub_button;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<SubButtonBean> getSub_button() {
            return sub_button;
        }

        public void setSub_button(List<SubButtonBean> sub_button) {
            this.sub_button = sub_button;
        }

        public static class SubButtonBean {
            /**
             * type : view
             * name : 搜索
             * url : http://www.soso.com/
             * appid : wx286b93c14bbf93aa
             * pagepath : pages/lunar/index
             * key : V1001_GOOD
             */

            private String type;
            private String name;
            private String url;
            private String appid;
            private String pagepath;
            private String key;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAppid() {
                return appid;
            }

            public void setAppid(String appid) {
                this.appid = appid;
            }

            public String getPagepath() {
                return pagepath;
            }

            public void setPagepath(String pagepath) {
                this.pagepath = pagepath;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }
    }
}
