package org.hzero.dd.dto;

public class SendIndependentCardMessageDTO extends SendMessageDTO {
    private IndependentCardFormat msg;

    public IndependentCardFormat getMsg() {
        return msg;
    }

    public void setMsg(IndependentCardFormat msg) {
        this.msg = msg;
    }
}
