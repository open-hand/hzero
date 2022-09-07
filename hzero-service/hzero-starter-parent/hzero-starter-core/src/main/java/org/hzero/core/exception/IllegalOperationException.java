package org.hzero.core.exception;

/**
 * 非法操作异常
 *
 * @author bojiangzhou 2018/08/31
 */
public class IllegalOperationException extends RuntimeException {
    private static final long serialVersionUID = 2352234475250862310L;

    public IllegalOperationException() {

    }

    public IllegalOperationException(String message) {
        super(message);
    }
}
