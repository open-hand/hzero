package org.hzero.wechat.dto;

import java.util.List;

public class GroupTextMessageByOpenIdDTO {

    /**
     * touser : ["OPENID1","OPENID2"]
     * msgtype : text
     * text : {"content":"hello from boxer."}
     */

    private String msgtype;
    private TextBean text;
    private List<String> touser;

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

    public List<String> getTouser() {
        return touser;
    }

    public void setTouser(List<String> touser) {
        this.touser = touser;
    }

    public static class TextBean {
        /**
         * content : hello from boxer.
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
