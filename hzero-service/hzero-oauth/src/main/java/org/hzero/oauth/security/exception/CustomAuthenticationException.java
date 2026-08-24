package org.hzero.oauth.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author dongfan117@gmail.com
 */
public class CustomAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -1790169796807241177L;

    private final transient Object[] parameters;

    public CustomAuthenticationException(String msg, Throwable t, Object... parameters) {
        super(msg, t);
        this.parameters = parameters;
    }

    public CustomAuthenticationException(String msg, Object... parameters) {
        super(msg);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

}
