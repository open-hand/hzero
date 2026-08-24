package org.hzero.wechat.dto;

public class PreviewVideoMessageDTO {

    /**
     * touser : OPENID
     * mpvideo : {"media_id":"IhdaAQXuvJtGzwwc0abfXnzeezfO0NgPK6AQYShD8RQYMTtfzbLdBIQkQziv2XJc"}
     * msgtype : mpvideo
     */

    private String touser;
    private MpvideoBean mpvideo;
    private String msgtype;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public MpvideoBean getMpvideo() {
        return mpvideo;
    }

    public void setMpvideo(MpvideoBean mpvideo) {
        this.mpvideo = mpvideo;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public static class MpvideoBean {
        /**
         * media_id : IhdaAQXuvJtGzwwc0abfXnzeezfO0NgPK6AQYShD8RQYMTtfzbLdBIQkQziv2XJc
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
