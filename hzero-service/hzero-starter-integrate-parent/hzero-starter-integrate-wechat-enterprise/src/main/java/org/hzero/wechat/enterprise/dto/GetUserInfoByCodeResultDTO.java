package org.hzero.wechat.enterprise.dto;

/**
 * Created by tx on 2019/12/11 14:15
 */

public class GetUserInfoByCodeResultDTO extends DefaultResultDTO {

    /**
     * errcode : 0
     * UserId : USERID
     * DeviceId : DEVICEID
     */


    private String UserId;
    private String DeviceId;
    private String OpenId;


    public String getOpenId() {
        return OpenId;
    }
    public void setOpenId(String openId) {
        OpenId = openId;
    }
    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String DeviceId) {
        this.DeviceId = DeviceId;
    }
}
