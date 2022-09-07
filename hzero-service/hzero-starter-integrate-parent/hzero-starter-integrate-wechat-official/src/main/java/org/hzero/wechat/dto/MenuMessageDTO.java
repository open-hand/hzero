package org.hzero.wechat.dto;

import java.util.List;

public class MenuMessageDTO {
    /**
     * touser : OPENID
     * msgtype : msgmenu
     * msgmenu : {"head_content":"您对本次服务是否满意呢? ","list":[{"id":"101","content":"满意"},{"id":"102","content":"不满意"}],"tail_content":"欢迎再次光临"}
     */

    private String touser;
    private String msgtype;
    private MsgmenuBean msgmenu;
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

    public MsgmenuBean getMsgmenu() {
        return msgmenu;
    }

    public void setMsgmenu(MsgmenuBean msgmenu) {
        this.msgmenu = msgmenu;
    }

    public static class MsgmenuBean {
        /**
         * head_content : 您对本次服务是否满意呢?
         * list : [{"id":"101","content":"满意"},{"id":"102","content":"不满意"}]
         * tail_content : 欢迎再次光临
         */

        private String head_content;
        private String tail_content;
        private List<ListBean> list;

        public String getHead_content() {
            return head_content;
        }

        public void setHead_content(String head_content) {
            this.head_content = head_content;
        }

        public String getTail_content() {
            return tail_content;
        }

        public void setTail_content(String tail_content) {
            this.tail_content = tail_content;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 101
             * content : 满意
             */

            private String id;
            private String content;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
