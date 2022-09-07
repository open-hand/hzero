package org.hzero.dd.dto;

public class SendTextMessageDTO extends  SendMessageDTO {
    private TextFormat msg;

    public TextFormat getMsg() {
        return msg;
    }

    public void setMsg(TextFormat msg) {
        this.msg = msg;
    }
}
