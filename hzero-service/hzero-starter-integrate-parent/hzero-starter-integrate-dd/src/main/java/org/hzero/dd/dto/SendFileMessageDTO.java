package org.hzero.dd.dto;

public class SendFileMessageDTO extends  SendMessageDTO {
    private FileFormat msg;

    public FileFormat getMsg() {
        return msg;
    }

    public void setMsg(FileFormat msg) {
        this.msg = msg;
    }
}
