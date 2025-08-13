package org.hzero.wechat.dto;

public class MusicMessageDTO {

    /**
     * touser : OPENID
     * msgtype : music
     * music : {"title":"MUSIC_TITLE","description":"MUSIC_DESCRIPTION","musicurl":"MUSIC_URL","hqmusicurl":"HQ_MUSIC_URL","thumb_media_id":"THUMB_MEDIA_ID"}
     */

    private String touser;
    private String msgtype;
    private MusicBean music;

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

    public MusicBean getMusic() {
        return music;
    }

    public void setMusic(MusicBean music) {
        this.music = music;
    }

    public static class MusicBean {
        /**
         * title : MUSIC_TITLE
         * description : MUSIC_DESCRIPTION
         * musicurl : MUSIC_URL
         * hqmusicurl : HQ_MUSIC_URL
         * thumb_media_id : THUMB_MEDIA_ID
         */

        private String title;
        private String description;
        private String musicurl;
        private String hqmusicurl;
        private String thumb_media_id;

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

        public String getMusicurl() {
            return musicurl;
        }

        public void setMusicurl(String musicurl) {
            this.musicurl = musicurl;
        }

        public String getHqmusicurl() {
            return hqmusicurl;
        }

        public void setHqmusicurl(String hqmusicurl) {
            this.hqmusicurl = hqmusicurl;
        }

        public String getThumb_media_id() {
            return thumb_media_id;
        }

        public void setThumb_media_id(String thumb_media_id) {
            this.thumb_media_id = thumb_media_id;
        }
    }
}
