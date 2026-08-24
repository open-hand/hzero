package org.hzero.dd.dto;

public class SendGroupLinkMessageDTO extends  SendGroupMessageDTO {

   private  LinkFormat  msg;

    public LinkFormat getMsg() {
        return msg;
    }

    public void setMsg(LinkFormat msg) {
        this.msg = msg;
    }
}
