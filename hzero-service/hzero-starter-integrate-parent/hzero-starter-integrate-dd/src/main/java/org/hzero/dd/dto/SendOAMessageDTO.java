package org.hzero.dd.dto;

public class SendOAMessageDTO extends SendMessageDTO {
    private OAFormat msg;

    public OAFormat getMsg() {
        return msg;
    }

    public void setMsg(OAFormat msg) {
        this.msg = msg;
    }
}
