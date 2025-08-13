package org.hzero.wechat.dto;

public class GroupImageMessageByMarkDTO {

    /**
     * filter : {"is_to_all":false,"tag_id":2}
     * image : {"media_id":"123dsdajkasd231jhksad"}
     * msgtype : image
     */

    private FilterBean filter;
    private ImageBean image;
    private String msgtype;

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
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

    public static class FilterBean {
        /**
         * is_to_all : false
         * tag_id : 2
         */

        private boolean is_to_all;
        private int tag_id;

        public boolean isIs_to_all() {
            return is_to_all;
        }

        public void setIs_to_all(boolean is_to_all) {
            this.is_to_all = is_to_all;
        }

        public int getTag_id() {
            return tag_id;
        }

        public void setTag_id(int tag_id) {
            this.tag_id = tag_id;
        }
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
