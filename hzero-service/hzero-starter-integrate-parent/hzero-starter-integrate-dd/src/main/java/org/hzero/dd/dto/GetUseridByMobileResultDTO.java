package org.hzero.dd.dto;

/**
 * Created by tx on 2019/11/23 20:14
 */

public class GetUseridByMobileResultDTO extends DefaultResultDTO {

    /**
     * userid : zhangsan
     */

    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
