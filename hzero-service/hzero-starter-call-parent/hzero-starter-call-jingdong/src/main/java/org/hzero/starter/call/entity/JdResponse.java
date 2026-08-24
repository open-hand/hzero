package org.hzero.starter.call.entity;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/27 17:37
 */
public class JdResponse {

    private String timestamp;
    private String messageId;

    public String getTimestamp() {
        return timestamp;
    }

    public JdResponse setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getMessageId() {
        return messageId;
    }

    public JdResponse setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }
}
