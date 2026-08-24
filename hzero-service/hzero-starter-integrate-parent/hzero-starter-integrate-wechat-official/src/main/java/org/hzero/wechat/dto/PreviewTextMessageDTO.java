package org.hzero.wechat.dto;

public class PreviewTextMessageDTO {

    /**
     * touser : OPENID
     * text : {"content":"CONTENT"}
     * msgtype : text
     */

    private String touser;
    private TextBean text;
    private String msgtype;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public static class TextBean {
        /**
         * content : CONTENT
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
