package org.hzero.wechat.dto;

public class WxCardMessageDTO {


    /**
     * touser : OPENID
     * msgtype : wxcard
     * wxcard : {"card_id":"123dsdajkasd231jhksad"}
     */

    private String touser;
    private String msgtype;
    private WxcardBean wxcard;

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

    public WxcardBean getWxcard() {
        return wxcard;
    }

    public void setWxcard(WxcardBean wxcard) {
        this.wxcard = wxcard;
    }

    public static class WxcardBean {
        /**
         * card_id : 123dsdajkasd231jhksad
         */

        private String card_id;

        public String getCard_id() {
            return card_id;
        }

        public void setCard_id(String card_id) {
            this.card_id = card_id;
        }
    }
}
