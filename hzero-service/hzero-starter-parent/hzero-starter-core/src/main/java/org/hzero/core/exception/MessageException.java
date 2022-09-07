package org.hzero.core.exception;

/**
 * 封装消息异常类
 *
 * @author bojiangzhou 2018/07/03
 */
public class MessageException extends RuntimeException {
    private static final long serialVersionUID = -6552875986668936643L;

    private final String code;

    public MessageException(String message) {
        super(message);
        this.code = null;
    }

    public MessageException(String message, String code) {
        super(message);
        this.code = code;
    }



    public String getCode() {
        return code;
    }
}
