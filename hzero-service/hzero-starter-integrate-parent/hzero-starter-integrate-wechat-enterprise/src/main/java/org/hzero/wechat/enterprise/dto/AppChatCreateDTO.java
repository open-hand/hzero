package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AppChatCreateDTO {

    /**
     * name : NAME
     * owner : userid1
     * userlist : ["userid1","userid2","userid3"]
     * chatid : CHATID
     */

    private String name;
    private String owner;
    private String chatid;
    private List<String> userlist;

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

    public List<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<String> userlist) {
        this.userlist = userlist;
    }
}
