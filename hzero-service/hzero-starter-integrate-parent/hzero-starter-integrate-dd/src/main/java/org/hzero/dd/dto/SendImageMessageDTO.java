package org.hzero.dd.dto;

public class SendImageMessageDTO extends SendMessageDTO {
    private ImageFormat msg;

    public ImageFormat getMsg() {
        return msg;
    }

    public void setMsg(ImageFormat msg) {
        this.msg = msg;
    }
}
