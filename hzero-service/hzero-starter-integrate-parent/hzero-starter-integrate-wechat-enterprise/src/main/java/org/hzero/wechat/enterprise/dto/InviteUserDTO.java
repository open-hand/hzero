package org.hzero.wechat.enterprise.dto;

import java.util.List;

public class InviteUserDTO {

    private List<String> user;
    private List<String> party;
    private List<String> tag;

    public List<String> getUser() {
        return user;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public List<String> getParty() {
        return party;
    }

    public void setParty(List<String> party) {
        this.party = party;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }
}
