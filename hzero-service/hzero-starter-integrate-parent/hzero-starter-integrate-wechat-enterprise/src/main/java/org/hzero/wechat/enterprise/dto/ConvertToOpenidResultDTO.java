package org.hzero.wechat.enterprise.dto;

public class ConvertToOpenidResultDTO extends DefaultResultDTO {
    private String openid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
