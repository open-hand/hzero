package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * Created by tx on 2019/11/24 22:17
 */

public class MenuDTO {

    private List<ButtonBean> button;

    public List<ButtonBean> getButton() {
        return button;
    }

    public void setButton(List<ButtonBean> button) {
        this.button = button;
    }

    public static class ButtonBean {
        /**
         * name : 扫码
         * sub_button : [{"type":"scancode_waitmsg","name":"扫码带提示","key":"rselfmenu_0_0","sub_button":[]},{"type":"scancode_push","name":"扫码推事件","key":"rselfmenu_0_1","sub_button":[]}]
         * type : location_select
         * key : rselfmenu_2_0
         */

        private String name;
        private String type;
        private String key;
        private List<SubButtonBean> sub_button;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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
             * type : scancode_waitmsg
             * name : 扫码带提示
             * key : rselfmenu_0_0
             * sub_button : []
             */

            private String type;
            private String name;
            private String key;
            private String url;
            private List<?> sub_button;

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

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<?> getSub_button() {
                return sub_button;
            }

            public void setSub_button(List<?> sub_button) {
                this.sub_button = sub_button;
            }
        }
    }
}
