package org.hzero.wechat.enterprise.dto;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class MessageSendResultDTO extends DefaultResultDTO {


    /**
     * errcode : 0
     * errmsg : ok
     * invaliduser : userid1|userid2
     * invalidparty : partyid1|partyid2
     * invalidtag : tagid1|tagid2
     */


    private String invaliduser;
    private String invalidparty;
    private String invalidtag;

    public String getInvaliduser() {
        return invaliduser;
    }

    public void setInvaliduser(String invaliduser) {
        this.invaliduser = invaliduser;
    }

    public String getInvalidparty() {
        return invalidparty;
    }

    public void setInvalidparty(String invalidparty) {
        this.invalidparty = invalidparty;
    }

    public String getInvalidtag() {
        return invalidtag;
    }

    public void setInvalidtag(String invalidtag) {
        this.invalidtag = invalidtag;
    }
}
