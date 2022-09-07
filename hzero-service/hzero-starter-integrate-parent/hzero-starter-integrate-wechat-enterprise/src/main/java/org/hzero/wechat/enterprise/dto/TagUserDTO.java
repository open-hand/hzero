package org.hzero.wechat.enterprise.dto;

import java.util.List;

public class TagUserDTO {

    /**
     * tagid : 12
     * userlist : ["user1","user2"]
     * partylist : [4]
     */

    private int tagid;
    private List<String> userlist;
    private List<Integer> partylist;

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public List<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<String> userlist) {
        this.userlist = userlist;
    }

    public List<Integer> getPartylist() {
        return partylist;
    }

    public void setPartylist(List<Integer> partylist) {
        this.partylist = partylist;
    }
}
