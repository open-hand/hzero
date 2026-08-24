package org.hzero.dd.dto;

public class LinkFormat {

    /**
     * msgtype : link
     * link : {"messageUrl":"http://s.dingtalk.com/market/dingtalk/error_code.php","picUrl":"@lALOACZwe2Rk","title":"测试","text":"测试"}
     */

    private String msgtype;
    private LinkBean link;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public LinkBean getLink() {
        return link;
    }

    public void setLink(LinkBean link) {
        this.link = link;
    }

    public static class LinkBean {
        /**
         * messageUrl : http://s.dingtalk.com/market/dingtalk/error_code.php
         * picUrl : @lALOACZwe2Rk
         * title : 测试
         * text : 测试
         */

        private String messageUrl;
        private String picUrl;
        private String title;
        private String text;

        public String getMessageUrl() {
            return messageUrl;
        }

        public void setMessageUrl(String messageUrl) {
            this.messageUrl = messageUrl;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
