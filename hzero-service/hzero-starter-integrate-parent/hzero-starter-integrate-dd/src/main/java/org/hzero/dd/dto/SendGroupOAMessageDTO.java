package org.hzero.dd.dto;

import java.util.List;

public class SendGroupOAMessageDTO  extends  SendGroupMessageDTO {

  private OAFormat msg;

    public OAFormat getMsg() {
        return msg;
    }

    public void setMsg(OAFormat msg) {
        this.msg = msg;
    }
}
