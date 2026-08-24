package org.hzero.wechat.dto;

public class PreviewMpNewsMessageDTO {

    /**
     * touser : OPENID
     * mpnews : {"media_id":"123dsdajkasd231jhksad"}
     * msgtype : mpnews
     */

    private String touser;
    private MpnewsBean mpnews;
    private String msgtype;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public MpnewsBean getMpnews() {
        return mpnews;
    }

    public void setMpnews(MpnewsBean mpnews) {
        this.mpnews = mpnews;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public static class MpnewsBean {
        /**
         * media_id : 123dsdajkasd231jhksad
         */

        private String media_id;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }
}
