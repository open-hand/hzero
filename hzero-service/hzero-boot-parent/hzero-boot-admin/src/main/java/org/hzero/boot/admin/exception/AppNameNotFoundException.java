package org.hzero.boot.admin.exception;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 5:29 下午
 */
public class AppNameNotFoundException extends RuntimeException {
    public AppNameNotFoundException() {
    }

    public AppNameNotFoundException(String message) {
        super(message);
    }

    public AppNameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppNameNotFoundException(Throwable cause) {
        super(cause);
    }

    public AppNameNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
