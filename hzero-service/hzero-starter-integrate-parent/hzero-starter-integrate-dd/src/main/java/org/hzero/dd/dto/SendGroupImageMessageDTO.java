package org.hzero.dd.dto;

public class SendGroupImageMessageDTO extends  SendGroupMessageDTO {
    private ImageFormat msg;

    public ImageFormat getMsg() {
        return msg;
    }

    public void setMsg(ImageFormat msg) {
        this.msg = msg;
    }
}
