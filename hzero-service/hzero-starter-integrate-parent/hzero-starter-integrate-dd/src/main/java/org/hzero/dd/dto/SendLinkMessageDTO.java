package org.hzero.dd.dto;

public class SendLinkMessageDTO extends  SendMessageDTO {
    private LinkFormat msg;

    public LinkFormat getMsg() {
        return msg;
    }

    public void setMsg(LinkFormat msg) {
        this.msg = msg;
    }
}
