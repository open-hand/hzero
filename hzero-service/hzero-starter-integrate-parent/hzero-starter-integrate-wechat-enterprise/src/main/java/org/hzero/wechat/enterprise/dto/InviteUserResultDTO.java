package org.hzero.wechat.enterprise.dto;

import java.util.List;

public class InviteUserResultDTO  {

    /**
     * errcode : 0
     * errmsg : ok
     * invaliduser : ["UserID1","UserID2"]
     * invalidparty : ["PartyID1","PartyID2"]
     * invalidtag : ["TagID1","TagID2"]
     */

    private List<String> invaliduser;
    private List<String> invalidparty;
    private List<String> invalidtag;

    public List<String> getInvaliduser() {
        return invaliduser;
    }

    public void setInvaliduser(List<String> invaliduser) {
        this.invaliduser = invaliduser;
    }

    public List<String> getInvalidparty() {
        return invalidparty;
    }

    public void setInvalidparty(List<String> invalidparty) {
        this.invalidparty = invalidparty;
    }

    public List<String> getInvalidtag() {
        return invalidtag;
    }

    public void setInvalidtag(List<String> invalidtag) {
        this.invalidtag = invalidtag;
    }
}
