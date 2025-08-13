package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AppChatDTO extends DefaultResultDTO{

    /**
     * errcode : 0
     * errmsg : ok
     * chat_info : {"chatid":"CHATID","name":"NAME","owner":"userid2","userlist":["userid1","userid2","userid3"]}
     */

    private ChatInfoBean chat_info;

    public ChatInfoBean getChat_info() {
        return chat_info;
    }

    public void setChat_info(ChatInfoBean chat_info) {
        this.chat_info = chat_info;
    }

    public static class ChatInfoBean {
        /**
         * chatid : CHATID
         * name : NAME
         * owner : userid2
         * userlist : ["userid1","userid2","userid3"]
         */

        private String chatid;
        private String name;
        private String owner;
        private List<String> userlist;

        public String getChatid() {
            return chatid;
        }

        public void setChatid(String chatid) {
            this.chatid = chatid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public List<String> getUserlist() {
            return userlist;
        }

        public void setUserlist(List<String> userlist) {
            this.userlist = userlist;
        }
    }
}
