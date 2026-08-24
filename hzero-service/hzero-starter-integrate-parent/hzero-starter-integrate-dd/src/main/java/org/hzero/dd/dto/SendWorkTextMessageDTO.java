package org.hzero.dd.dto;

public class SendWorkTextMessageDTO extends SendWorkMessageDTO {
   private TextFormat msg;

    public TextFormat getMsg() {
        return msg;
    }

    public void setMsg(TextFormat msg) {
        this.msg = msg;
    }
}
