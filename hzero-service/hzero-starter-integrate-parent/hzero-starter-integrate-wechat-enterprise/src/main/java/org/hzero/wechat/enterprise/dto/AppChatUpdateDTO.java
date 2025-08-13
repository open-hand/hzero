package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class AppChatUpdateDTO {


    /**
     * chatid : CHATID
     * name : NAME
     * owner : userid2
     * add_user_list : ["userid1","userid2","userid3"]
     * del_user_list : ["userid3","userid4"]
     */

    private String chatid;
    private String name;
    private String owner;
    private List<String> add_user_list;
    private List<String> del_user_list;

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

    public List<String> getAdd_user_list() {
        return add_user_list;
    }

    public void setAdd_user_list(List<String> add_user_list) {
        this.add_user_list = add_user_list;
    }

    public List<String> getDel_user_list() {
        return del_user_list;
    }

    public void setDel_user_list(List<String> del_user_list) {
        this.del_user_list = del_user_list;
    }
}
