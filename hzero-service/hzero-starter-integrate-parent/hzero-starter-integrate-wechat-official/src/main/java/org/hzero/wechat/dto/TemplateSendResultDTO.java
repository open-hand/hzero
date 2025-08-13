package org.hzero.wechat.dto;

/**
 * @Author J
 * @Date 2019/10/15
 */
public class TemplateSendResultDTO extends DefaultResultDTO{


    /**
     * errcode : 0
     * errmsg : ok
     * msgid : 200228332
     */

    private Long msgid;

    public Long getMsgid() {
        return msgid;
    }

    public TemplateSendResultDTO setMsgid(Long msgid) {
        this.msgid = msgid;
        return this;
    }
}
