package org.hzero.dd.dto;

public class SendGroupTextMessageDTO extends SendGroupMessageDTO {

   private TextFormat msg ;

    public TextFormat getMsg() {
        return msg;
    }

    public void setMsg(TextFormat msg) {
        this.msg = msg;
    }
}
