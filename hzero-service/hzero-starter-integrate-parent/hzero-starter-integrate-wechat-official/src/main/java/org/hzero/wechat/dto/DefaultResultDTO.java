package org.hzero.wechat.dto;

/**
 * @Author J
 * @Date 2019/10/15
 */
public class DefaultResultDTO {


    /**
     * errcode : 0
     * errmsg : ok
     */

    private Long errcode;
    private String errmsg;


    public Long getErrcode() {
        return errcode;
    }

    public DefaultResultDTO setErrcode(Long errcode) {
        this.errcode = errcode;
        return this;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}
