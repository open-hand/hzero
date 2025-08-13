package org.hzero.message.infra.exception;

/**
 * <p>
 * 自定义发送消息异常
 * </p>
 *
 * @author qingsheng.chen 2018/9/5 星期三 16:19
 */
public class SendMessageException extends RuntimeException {

    public SendMessageException(String message) {
        super(message);
    }
}
