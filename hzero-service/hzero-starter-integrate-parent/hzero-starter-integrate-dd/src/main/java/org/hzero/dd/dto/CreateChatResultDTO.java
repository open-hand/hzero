package org.hzero.dd.dto;

public class CreateChatResultDTO extends DefaultResultDTO{
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "chatid": "chatxxxxxxxxxxxxxxxxxxx",
     *     "conversationTag": 2
     */
    private String chatid;
    private int conversationTag;


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
}
