package org.hzero.starter.social.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户未绑定异常
 *
 * @author bojiangzhou 2018/10/28
 */
public class UserUnbindException extends AuthenticationException {
    private static final long serialVersionUID = 1328204299909483785L;

    public UserUnbindException(String msg) {
        super(msg);
    }

    public UserUnbindException(String msg, Throwable t) {
        super(msg, t);
    }
}
