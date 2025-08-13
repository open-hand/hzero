package org.hzero.wechat.dto;

import java.util.List;

public class GroupVideoMessageByOpenIdDTO {

    /**
     * touser : ["OPENID1","OPENID2"]
     * mpvideo : {"media_id":"123dsdajkasd231jhksad","title":"TITLE","description":"DESCRIPTION"}
     * msgtype : mpvideo
     */

    private MpvideoBean mpvideo;
    private String msgtype;
    private List<String> touser;

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

    public List<String> getTouser() {
        return touser;
    }

    public void setTouser(List<String> touser) {
        this.touser = touser;
    }

    public static class MpvideoBean {
        /**
         * media_id : 123dsdajkasd231jhksad
         * title : TITLE
         * description : DESCRIPTION
         */

        private String media_id;
        private String title;
        private String description;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
