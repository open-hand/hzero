package org.hzero.dd.dto;

public class SendWorkMarkDownMessageDTO extends SendWorkMessageDTO{
    private MarkDownFormat msg;

    public MarkDownFormat getMsg() {
        return msg;
    }

    public void setMsg(MarkDownFormat msg) {
        this.msg = msg;
    }
}
