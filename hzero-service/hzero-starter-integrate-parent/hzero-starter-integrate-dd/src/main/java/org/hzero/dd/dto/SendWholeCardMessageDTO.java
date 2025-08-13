package org.hzero.dd.dto;

public class SendWholeCardMessageDTO extends  SendMessageDTO {

    private  WholeFormat msg;

    public WholeFormat getMsg() {
        return msg;
    }

    public void setMsg(WholeFormat msg) {
        this.msg = msg;
    }
}
