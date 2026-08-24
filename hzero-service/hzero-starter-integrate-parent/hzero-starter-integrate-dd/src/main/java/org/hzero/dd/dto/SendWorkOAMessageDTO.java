package org.hzero.dd.dto;

import java.util.List;

public class SendWorkOAMessageDTO extends  SendWorkMessageDTO {

   private OAFormat msg;

    public OAFormat getMsg() {
        return msg;
    }

    public void setMsg(OAFormat msg) {
        this.msg = msg;
    }
}
