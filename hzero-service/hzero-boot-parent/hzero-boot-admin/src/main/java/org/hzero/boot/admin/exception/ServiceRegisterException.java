package org.hzero.boot.admin.exception;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 7:41 下午
 */
public class ServiceRegisterException extends RuntimeException {
    public ServiceRegisterException() {
    }

    public ServiceRegisterException(String message) {
        super(message);
    }

    public ServiceRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceRegisterException(Throwable cause) {
        super(cause);
    }

    public ServiceRegisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
