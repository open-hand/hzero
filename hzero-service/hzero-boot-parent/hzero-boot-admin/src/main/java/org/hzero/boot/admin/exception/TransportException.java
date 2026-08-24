package org.hzero.boot.admin.exception;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 9:00 上午
 */
public class TransportException extends Exception {

    public TransportException() {
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }

    public TransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
