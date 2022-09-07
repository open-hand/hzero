package org.hzero.wechat.dto;

import java.util.List;

public class GroupWxCardMessageByOpenIdDTO {
    /**
     * touser : ["OPENID1","OPENID2"]
     * wxcard : {"card_id":"123dsdajkasd231jhksad"}
     * msgtype : wxcard
     */

    private WxcardBean wxcard;
    private String msgtype;
    private List<String> touser;

    public WxcardBean getWxcard() {
        return wxcard;
    }

    public void setWxcard(WxcardBean wxcard) {
        this.wxcard = wxcard;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public List<String> getTouser() {
        return touser;
    }

    public void setTouser(List<String> touser) {
        this.touser = touser;
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
