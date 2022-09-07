package org.hzero.oauth.security.exception;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

/**
 * Auth2 请求异常
 *
 * @author bojiangzhou 2019/02/25
 */
public class Auth2RequestException extends InternalAuthenticationServiceException {

    public Auth2RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public Auth2RequestException(String message) {
        super(message);
    }

}
