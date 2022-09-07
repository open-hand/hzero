package org.hzero.dd.dto;

/**
 * @Author J
 * @Date 2019/8/29
 */
public class TokenDTO {


    /**
     * errcode : 0
     * errmsg : ok
     * access_token : accesstoken000001
     * expires_in : 7200
     */

    private int errcode;
    private String errmsg;
    private String access_token;
    private int expires_in;


    public int getErrcode() {
        return errcode;
    }

    public TokenDTO setErrcode(int errcode) {
        this.errcode = errcode;
        return this;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public TokenDTO setErrmsg(String errmsg) {
        this.errmsg = errmsg;
        return this;
    }

    public String getAccess_token() {
        return access_token;
    }

    public TokenDTO setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public TokenDTO setExpires_in(int expires_in) {
        this.expires_in = expires_in;
        return this;
    }
}
