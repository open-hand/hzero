package org.hzero.boot.message.entity;

import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 远程服务消息传输对象
 * </p>
 *
 * @author qingsheng.chen 2018/8/8 星期三 9:10
 */
public class MessageTransmission extends Message {
    private String messageTemplateCode;
    private Map<String, String> args;

    public String getMessageTemplateCode() {
        return messageTemplateCode;
    }

    public MessageTransmission setMessageTemplateCode(String messageTemplateCode) {
        this.messageTemplateCode = messageTemplateCode;
        return this;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public MessageTransmission setArgs(Map<String, String> args) {
        this.args = args;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageTransmission)) return false;
        if (!super.equals(o)) return false;
        MessageTransmission that = (MessageTransmission) o;
        return Objects.equals(messageTemplateCode, that.messageTemplateCode) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messageTemplateCode, args);
    }

    @Override
    public String toString() {
        return "MessageTransmission{" +
                "messageTemplateCode='" + messageTemplateCode + '\'' +
                ", args=" + args +
                "} " + super.toString();
    }
}
