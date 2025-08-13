package org.hzero.wechat.dto;

public class ImageMassageDTO {

    /**
     * touser : OPENID
     * msgtype : image
     * image : {"media_id":"MEDIA_ID"}
     */

    private String touser;
    private String msgtype;
    private ImageBean image;

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

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public static class ImageBean {
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
