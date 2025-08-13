package org.hzero.wechat.dto;

public class MiniProgramPageMessageDTO {

    /**
     * touser : OPENID
     * msgtype : miniprogrampage
     * miniprogrampage : {"title":"title","appid":"appid","pagepath":"pagepath","thumb_media_id":"thumb_media_id"}
     */

    private String touser;
    private String msgtype;
    private MiniprogrampageBean miniprogrampage;

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

    public MiniprogrampageBean getMiniprogrampage() {
        return miniprogrampage;
    }

    public void setMiniprogrampage(MiniprogrampageBean miniprogrampage) {
        this.miniprogrampage = miniprogrampage;
    }

    public static class MiniprogrampageBean {
        /**
         * title : title
         * appid : appid
         * pagepath : pagepath
         * thumb_media_id : thumb_media_id
         */

        private String title;
        private String appid;
        private String pagepath;
        private String thumb_media_id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPagepath() {
            return pagepath;
        }

        public void setPagepath(String pagepath) {
            this.pagepath = pagepath;
        }

        public String getThumb_media_id() {
            return thumb_media_id;
        }

        public void setThumb_media_id(String thumb_media_id) {
            this.thumb_media_id = thumb_media_id;
        }
    }
}
