package org.hzero.dd.dto;

public class UserCreateResultDTO extends DefaultResultDTO {
    /**
     * "errcode": 0,
     * "errmsg": "ok",
     * "userid": "zhangsan"
     */


    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
