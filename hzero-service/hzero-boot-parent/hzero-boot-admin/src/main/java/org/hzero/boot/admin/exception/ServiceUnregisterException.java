package org.hzero.boot.admin.exception;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 7:41 下午
 */
public class ServiceUnregisterException extends RuntimeException {
    public ServiceUnregisterException() {
    }

    public ServiceUnregisterException(String message) {
        super(message);
    }

    public ServiceUnregisterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnregisterException(Throwable cause) {
        super(cause);
    }

    public ServiceUnregisterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
