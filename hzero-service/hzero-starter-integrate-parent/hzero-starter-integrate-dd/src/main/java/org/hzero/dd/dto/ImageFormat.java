package org.hzero.dd.dto;

public class ImageFormat {

    /**
     * msgtype : image
     * image : {"media_id":"MEDIA_ID"}
     */

    private String msgtype;
    private ImageBean image;

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
