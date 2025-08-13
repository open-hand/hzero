package org.hzero.dd.dto;

public class WholeFormat {

    /**
     * msgtype : action_card
     * action_card : {"title":"是透出到会话列表和通知的文案","markdown":"支持markdown格式的正文内容","single_title":"查看详情","single_url":"https://open.dingtalk.com"}
     */

    private String msgtype;
    private ActionCardBean action_card;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public ActionCardBean getAction_card() {
        return action_card;
    }

    public void setAction_card(ActionCardBean action_card) {
        this.action_card = action_card;
    }

    public static class ActionCardBean {
        /**
         * title : 是透出到会话列表和通知的文案
         * markdown : 支持markdown格式的正文内容
         * single_title : 查看详情
         * single_url : https://open.dingtalk.com
         */

        private String title;
        private String markdown;
        private String single_title;
        private String single_url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMarkdown() {
            return markdown;
        }

        public void setMarkdown(String markdown) {
            this.markdown = markdown;
        }

        public String getSingle_title() {
            return single_title;
        }

        public void setSingle_title(String single_title) {
            this.single_title = single_title;
        }

        public String getSingle_url() {
            return single_url;
        }

        public void setSingle_url(String single_url) {
            this.single_url = single_url;
        }
    }
}
