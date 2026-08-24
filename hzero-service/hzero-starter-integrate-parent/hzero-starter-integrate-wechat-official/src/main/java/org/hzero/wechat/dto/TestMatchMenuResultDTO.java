package org.hzero.wechat.dto;

import java.util.List;

public class TestMatchMenuResultDTO {

    private List<ButtonBean> button;

    public List<ButtonBean> getButton() {
        return button;
    }

    public void setButton(List<ButtonBean> button) {
        this.button = button;
    }

    public static class ButtonBean {
        /**
         * type : view
         * name : tx
         * url : http://www.qq.com/
         * sub_button : []
         */

        private String type;
        private String name;
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
