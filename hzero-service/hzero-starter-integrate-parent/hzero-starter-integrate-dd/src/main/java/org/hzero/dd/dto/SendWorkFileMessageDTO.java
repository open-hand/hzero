package org.hzero.dd.dto;

public class SendWorkFileMessageDTO extends  SendWorkMessageDTO {

   private FileFormat msg;

    public FileFormat getMsg() {
        return msg;
    }

    public void setMsg(FileFormat msg) {
        this.msg = msg;
    }
}
