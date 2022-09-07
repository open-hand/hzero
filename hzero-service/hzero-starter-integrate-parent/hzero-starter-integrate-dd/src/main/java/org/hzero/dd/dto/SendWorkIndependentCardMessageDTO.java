package org.hzero.dd.dto;


public class SendWorkIndependentCardMessageDTO extends SendWorkMessageDTO {

    private IndependentCardFormat msg;

    public IndependentCardFormat getMsg() {
        return msg;
    }

    public void setMsg(IndependentCardFormat msg) {
        this.msg = msg;
    }
}
