package org.hzero.wechat.dto;

public class TextMessageDTO {


    /**
     * touser : OPENID
     * msgtype : text
     * text : {"content":"Hello World"}
     * customservice : {"kf_account":"test1@kftest"}
     */

    private String touser;
    private String msgtype;
    private TextBean text;
    private CustomserviceBean customservice;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public CustomserviceBean getCustomservice() {
        return customservice;
    }

    public void setCustomservice(CustomserviceBean customservice) {
        this.customservice = customservice;
    }

    public static class TextBean {
        /**
         * content : Hello World
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class CustomserviceBean {
        /**
         * kf_account : test1@kftest
         */

        private String kf_account;

        public String getKf_account() {
            return kf_account;
        }

        public void setKf_account(String kf_account) {
            this.kf_account = kf_account;
        }
    }
}
