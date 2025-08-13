package org.hzero.starter.social.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户已绑定异常
 *
 * @author bojiangzhou 2018/10/28
 */
public class UserBindException extends AuthenticationException {
    private static final long serialVersionUID = -190426375127566576L;

    public UserBindException(String msg) {
        super(msg);
    }

    public UserBindException(String msg, Throwable t) {
        super(msg, t);
    }
}
