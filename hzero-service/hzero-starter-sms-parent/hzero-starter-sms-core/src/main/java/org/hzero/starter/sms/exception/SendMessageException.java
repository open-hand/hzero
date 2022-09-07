package org.hzero.starter.sms.exception;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 19:47
 */
public class SendMessageException extends RuntimeException {

    public SendMessageException(String message) {
        super(message);
    }
}