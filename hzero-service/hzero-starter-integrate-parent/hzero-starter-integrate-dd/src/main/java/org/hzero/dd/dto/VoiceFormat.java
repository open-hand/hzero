package org.hzero.dd.dto;

public class VoiceFormat {

    /**
     * msgtype : voice
     * voice : {"media_id":"MEDIA_ID","duration":"10"}
     */

    private String msgtype;
    private VoiceBean voice;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceBean voice) {
        this.voice = voice;
    }

    public static class VoiceBean {
        /**
         * media_id : MEDIA_ID
         * duration : 10
         */

        private String media_id;
        private String duration;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
