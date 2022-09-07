package org.hzero.wechat.dto;

public class MpNewsMessageDTO {

    /**
     * touser : OPENID
     * msgtype : mpnews
     * mpnews : {"media_id":"MEDIA_ID"}
     */

    private String touser;
    private String msgtype;
    private MpnewsBean mpnews;

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

    public MpnewsBean getMpnews() {
        return mpnews;
    }

    public void setMpnews(MpnewsBean mpnews) {
        this.mpnews = mpnews;
    }

    public static class MpnewsBean {
        /**
         * media_id : MEDIA_ID
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
