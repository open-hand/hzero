package org.hzero.dd.dto;

public class  SendMessageResultDTO extends DefaultResultDTO {
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "receiver": "UserID1|UserID2"
     */

    private  String receiver;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
