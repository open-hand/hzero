package org.hzero.dd.dto;

/**
 * Created by tx on 2019/11/23 20:12
 */

public class GetUseridByUnionidResultDTO extends DefaultResultDTO {

    /**
     * contactType : 0
     * userid : userid1
     */

    private int contactType;
    private String userid;

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
