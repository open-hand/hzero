package org.hzero.dd.dto;

public class SendMarkDownMessageDTO extends SendMessageDTO {
    private MarkDownFormat msg;

    public MarkDownFormat getMsg() {
        return msg;
    }

    public void setMsg(MarkDownFormat msg) {
        this.msg = msg;
    }
}
