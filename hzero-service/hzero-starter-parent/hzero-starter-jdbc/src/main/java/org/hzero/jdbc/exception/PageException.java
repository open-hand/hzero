package org.hzero.jdbc.exception;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class PageException extends RuntimeException {

    public PageException(String message) {
        super(message);
    }

    public PageException(String message, Throwable cause) {
        super(message, cause);
    }
}
