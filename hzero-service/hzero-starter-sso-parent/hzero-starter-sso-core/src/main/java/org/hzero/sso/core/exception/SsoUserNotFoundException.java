package org.hzero.sso.core.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 向SSO服务器获取用户失败
 *
 * @author bojiangzhou 2020/08/19
 */
public class SsoUserNotFoundException extends AuthenticationException {
    private static final long serialVersionUID = -1392614586292018242L;

    public SsoUserNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public SsoUserNotFoundException(String msg) {
        super(msg);
    }

    public SsoUserNotFoundException() {
        super(SsoLoginExceptions.SSO_SERVER_USER_NOT_FOUND.value());
    }
}
