package org.hzero.wechat.dto;

public class PreviewImageMessageDTO {

    /**
     * touser : OPENID
     * image : {"media_id":"123dsdajkasd231jhksad"}
     * msgtype : image
     */

    private String touser;
    private ImageBean image;
    private String msgtype;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public ImageBean getImage() {
        return image;
    }

    public void setImage(ImageBean image) {
        this.image = image;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public static class ImageBean {
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
