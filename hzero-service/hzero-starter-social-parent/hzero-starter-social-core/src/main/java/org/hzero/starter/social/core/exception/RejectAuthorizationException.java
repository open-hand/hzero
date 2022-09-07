package org.hzero.starter.social.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户拒绝授权
 *
 * @author bojiangzhou 2018/10/28
 */
public class RejectAuthorizationException extends AuthenticationException {
    private static final long serialVersionUID = -8394775968068232175L;

    public RejectAuthorizationException(String msg) {
        super(msg);
    }

    public RejectAuthorizationException(String msg, Throwable t) {
        super(msg, t);
    }
}
