package org.hzero.sso.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * SSO 服务异常
 *
 * @author bojiangzhou 2020/08/19
 */
public class SsoServiceException extends AuthenticationException {
    private static final long serialVersionUID = 8077605398616663558L;

    public SsoServiceException(String msg, Throwable t) {
        super(msg, t);
    }

    public SsoServiceException(String msg) {
        super(msg);
    }

}
