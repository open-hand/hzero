package org.hzero.wechat.dto;

/**
 * @Author J
 * @Date 2019/8/29
 */
public class TokenDTO extends DefaultResultDTO {


    /**
     * errcode : 0
     * errmsg : ok
     * access_token : accesstoken000001
     * expires_in : 7200
     */


    private String access_token;
    private Long expires_in;


    public String getAccess_token() {
        return access_token;
    }

    public TokenDTO setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public TokenDTO setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
        return this;
    }
}
