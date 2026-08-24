package org.hzero.dd.dto;

import java.util.List;

public class IndependentCardFormat {

    /**
     * msgtype : action_card
     * action_card : {"title":"是透出到会话列表和通知的文案","markdown":"支持markdown格式的正文内容","btn_orientation":"1","btn_json_list":[{"title":"一个按钮","action_url":"https://www.taobao.com"},{"title":"两个按钮","action_url":"https://www.tmall.com"}]}
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
         * btn_orientation : 1
         * btn_json_list : [{"title":"一个按钮","action_url":"https://www.taobao.com"},{"title":"两个按钮","action_url":"https://www.tmall.com"}]
         */

        private String title;
        private String markdown;
        private String btn_orientation;
        private List<BtnJsonListBean> btn_json_list;

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

        public String getBtn_orientation() {
            return btn_orientation;
        }

        public void setBtn_orientation(String btn_orientation) {
            this.btn_orientation = btn_orientation;
        }

        public List<BtnJsonListBean> getBtn_json_list() {
            return btn_json_list;
        }

        public void setBtn_json_list(List<BtnJsonListBean> btn_json_list) {
            this.btn_json_list = btn_json_list;
        }

        public static class BtnJsonListBean {
            /**
             * title : 一个按钮
             * action_url : https://www.taobao.com
             */

            private String title;
            private String action_url;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAction_url() {
                return action_url;
            }

            public void setAction_url(String action_url) {
                this.action_url = action_url;
            }
        }
    }
}
