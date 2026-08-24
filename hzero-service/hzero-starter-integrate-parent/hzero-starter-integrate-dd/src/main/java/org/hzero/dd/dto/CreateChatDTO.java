package org.hzero.dd.dto;
import java.util.*;
public class CreateChatDTO {
    private String name;
    private String owner;
    private List<String> useridlist;
    private Integer showHistoryType;
    private Integer searchable;
    private Integer validationType;
    private Integer mentionAllAuthority;
    private Integer chatBannedType;
    private Integer managementType;

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

    public List<String> getUseridlist() {
        return useridlist;
    }

    public void setUseridlist(List<String> useridlist) {
        this.useridlist = useridlist;
    }

    public Integer getShowHistoryType() {
        return showHistoryType;
    }

    public void setShowHistoryType(Integer showHistoryType) {
        this.showHistoryType = showHistoryType;
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

    public Integer getChatBannedType() {
        return chatBannedType;
    }

    public void setChatBannedType(Integer chatBannedType) {
        this.chatBannedType = chatBannedType;
    }

    public Integer getManagementType() {
        return managementType;
    }

    public void setManagementType(Integer managementType) {
        this.managementType = managementType;
    }
}
