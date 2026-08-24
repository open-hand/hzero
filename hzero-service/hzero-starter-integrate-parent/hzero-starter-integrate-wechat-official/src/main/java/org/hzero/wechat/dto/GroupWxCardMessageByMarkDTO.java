package org.hzero.wechat.dto;

public class GroupWxCardMessageByMarkDTO {

    /**
     * filter : {"is_to_all":false,"tag_id":"2"}
     * wxcard : {"card_id":"123dsdajkasd231jhksad"}
     * msgtype : wxcard
     */

    private FilterBean filter;
    private WxcardBean wxcard;
    private String msgtype;

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
    }

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

    public static class FilterBean {
        /**
         * is_to_all : false
         * tag_id : 2
         */

        private boolean is_to_all;
        private String tag_id;

        public boolean isIs_to_all() {
            return is_to_all;
        }

        public void setIs_to_all(boolean is_to_all) {
            this.is_to_all = is_to_all;
        }

        public String getTag_id() {
            return tag_id;
        }

        public void setTag_id(String tag_id) {
            this.tag_id = tag_id;
        }
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
