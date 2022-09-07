package org.hzero.dd.dto;

public class SendGroupWholeCardMessageDTO extends  SendGroupMessageDTO {

    private WholeFormat msg;

    public WholeFormat getMsg() {
        return msg;
    }

    public void setMsg(WholeFormat msg) {
        this.msg = msg;
    }
}
