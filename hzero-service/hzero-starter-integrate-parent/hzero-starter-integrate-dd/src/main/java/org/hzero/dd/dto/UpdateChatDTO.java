package org.hzero.dd.dto;
import java.util.*;

public class UpdateChatDTO {
    private String chatid;
    private String name;
    private String owner;
    private List<String> add_useridlist;
    private List<String> del_useridlist;
    private String icon;
    private Integer chatBannedType;
    private Integer searchable;
    private Integer validationType;
    private Integer mentionAllAuthority;
    private Integer showHistoryType;
    private Integer managementType;

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

    public List<String> getAdd_useridlist() {
        return add_useridlist;
    }

    public void setAdd_useridlist(List<String> add_useridlist) {
        this.add_useridlist = add_useridlist;
    }

    public List<String> getDel_useridlist() {
        return del_useridlist;
    }

    public void setDel_useridlist(List<String> del_useridlist) {
        this.del_useridlist = del_useridlist;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getChatBannedType() {
        return chatBannedType;
    }

    public void setChatBannedType(Integer chatBannedType) {
        this.chatBannedType = chatBannedType;
    }

    public Integer getSearchable() {
        return searchable;
    }

    public void setSearchable(Integer searchable) {
        this.searchable = searchable;
    }

    public Integer getValidationType() {
        return validationType;
    }

    public void setValidationType(Integer validationType) {
        this.validationType = validationType;
    }

    public Integer getMentionAllAuthority() {
        return mentionAllAuthority;
    }

    public void setMentionAllAuthority(Integer mentionAllAuthority) {
        this.mentionAllAuthority = mentionAllAuthority;
    }

    public Integer getShowHistoryType() {
        return showHistoryType;
    }

    public void setShowHistoryType(Integer showHistoryType) {
        this.showHistoryType = showHistoryType;
    }

    public Integer getManagementType() {
        return managementType;
    }

    public void setManagementType(Integer managementType) {
        this.managementType = managementType;
    }
}
