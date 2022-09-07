package org.hzero.dd.dto;

/**
 * Created by tx on 2019/12/11 16:33
 */

public class GetUserInfoByCodeResultDTO extends DefaultResultDTO {
//             "userid": "****",
//             "sys_level": 1,
//             "errmsg": "ok",
//             "is_sys": true,
//             "errcode": 0

                private  String userid;
                private  long sys_level;
                private  Boolean is_sys;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getSys_level() {
        return sys_level;
    }

    public void setSys_level(long sys_level) {
        this.sys_level = sys_level;
    }

    public Boolean getIs_sys() {
        return is_sys;
    }

    public void setIs_sys(Boolean is_sys) {
        this.is_sys = is_sys;
    }
}
