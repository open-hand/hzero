package org.hzero.dd.dto;

public class SendGroupFileMessageDTO extends SendGroupMessageDTO {

    private FileFormat msg;

    public FileFormat getMsg() {
        return msg;
    }

    public void setMsg(FileFormat msg) {
        this.msg = msg;
    }
}
