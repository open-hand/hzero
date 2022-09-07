package org.hzero.wechat.dto;

public class GroupVideoMessageByMarkDTO {

    /**
     * filter : {"is_to_all":false,"tag_id":2}
     * mpvideo : {"media_id":"IhdaAQXuvJtGzwwc0abfXnzeezfO0NgPK6AQYShD8RQYMTtfzbLdBIQkQziv2XJc"}
     * msgtype : mpvideo
     */

    private FilterBean filter;
    private MpvideoBean mpvideo;
    private String msgtype;

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
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
