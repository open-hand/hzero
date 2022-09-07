package org.hzero.dd.dto;

import java.util.List;

public class GetChatResultDTO  extends DefaultResultDTO{
    /**
     * chat_info : {"name":"GroupName","owner":"zhangsan","chatid":"chatxxxxxxxxxxxxxxxxxxxxxxxx","conversationTag":2,"useridlist":["zhangsan","lisi"]}
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
         * name : GroupName
         * owner : zhangsan
         * chatid : chatxxxxxxxxxxxxxxxxxxxxxxxx
         * conversationTag : 2
         * useridlist : ["zhangsan","lisi"]
         */

        private String name;
        private String owner;
        private String chatid;
        private int conversationTag;
        private List<String> useridlist;

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

        public String getChatid() {
            return chatid;
        }

        public void setChatid(String chatid) {
            this.chatid = chatid;
        }

        public int getConversationTag() {
            return conversationTag;
        }

        public void setConversationTag(int conversationTag) {
            this.conversationTag = conversationTag;
        }

        public List<String> getUseridlist() {
            return useridlist;
        }

        public void setUseridlist(List<String> useridlist) {
            this.useridlist = useridlist;
        }
    }

}
