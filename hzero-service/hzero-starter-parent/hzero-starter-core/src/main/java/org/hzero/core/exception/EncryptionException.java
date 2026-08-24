package org.hzero.core.exception;

/**
 * <p>
 * 加密异常
 * </p>
 *
 * @author qingsheng.chen 2018/11/29 星期四 19:58
 */
public class EncryptionException extends RuntimeException{

    private static final long serialVersionUID = 5226701172571334642L;

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
