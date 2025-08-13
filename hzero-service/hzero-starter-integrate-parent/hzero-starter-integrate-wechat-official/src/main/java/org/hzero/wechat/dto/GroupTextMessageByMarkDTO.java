package org.hzero.wechat.dto;

public class GroupTextMessageByMarkDTO {

    /**
     * filter : {"is_to_all":false,"tag_id":2}
     * text : {"content":"CONTENT"}
     * msgtype : text
     */

    private FilterBean filter;
    private TextBean text;
    private String msgtype;

    public FilterBean getFilter() {
        return filter;
    }

    public void setFilter(FilterBean filter) {
        this.filter = filter;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
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

    public static class TextBean {
        /**
         * content : CONTENT
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
