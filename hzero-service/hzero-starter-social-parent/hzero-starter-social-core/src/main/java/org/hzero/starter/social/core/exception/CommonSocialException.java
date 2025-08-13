package org.hzero.starter.social.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 三方账户异常
 *
 * @author bojiangzhou 2018/10/28
 */
public class CommonSocialException extends AuthenticationException {
    private static final long serialVersionUID = 279554864353712056L;

    public CommonSocialException(String msg) {
        super(msg);
    }

    public CommonSocialException(String msg, Throwable t) {
        super(msg, t);
    }
}
