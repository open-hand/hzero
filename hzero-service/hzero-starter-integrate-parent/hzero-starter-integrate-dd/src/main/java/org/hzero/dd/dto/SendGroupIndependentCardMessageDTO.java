package org.hzero.dd.dto;

import java.util.List;

public class SendGroupIndependentCardMessageDTO extends SendGroupMessageDTO {

   private IndependentCardFormat msg;

    public IndependentCardFormat getMsg() {
        return msg;
    }

    public void setMsg(IndependentCardFormat msg) {
        this.msg = msg;
    }
}
