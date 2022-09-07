package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AppChatCreateResultDTO extends DefaultResultDTO{


    /**
     * errcode : 0
     * errmsg : ok
     * chatid : CHATID
     */


    private String chatid;


    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }
}
