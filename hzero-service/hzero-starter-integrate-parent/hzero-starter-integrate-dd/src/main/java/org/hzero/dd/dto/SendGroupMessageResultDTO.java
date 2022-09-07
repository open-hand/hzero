package org.hzero.dd.dto;

public class SendGroupMessageResultDTO extends DefaultResultDTO{
    /**
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "messageId":"abcd"
     */

    private String messageId;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
