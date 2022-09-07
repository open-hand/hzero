package org.hzero.sso.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * SSO 服务器响应异常
 *
 * @author bojiangzhou 2020/08/19
 */
public class SsoServerException extends AuthenticationException {
    private static final long serialVersionUID = -1392614586292018242L;

    public SsoServerException(String msg, Throwable t) {
        super(msg, t);
    }

    public SsoServerException(String msg) {
        super(msg);
    }

    public SsoServerException(Throwable t) {
        super(SsoLoginExceptions.SSO_SERVER_ERROR.value(), t);
    }
}
