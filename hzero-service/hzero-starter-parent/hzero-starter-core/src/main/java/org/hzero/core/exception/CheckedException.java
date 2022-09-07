package org.hzero.core.exception;

/**
 * 受检查异常
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/19 9:33
 */
public class CheckedException extends Exception {
    private static final long serialVersionUID = 7638819698564041064L;
    
    private final transient Object[] parameters;

    /**
     * 构造器
     *
     * @param message 异常信息
     * @param parameters parameters
     */
    public CheckedException(String message, Object... parameters) {

        super(message);
        this.parameters = parameters;
    }

    public CheckedException(Throwable cause, Object... parameters) {
        super(cause);
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }
}
