package org.hzero.oauth.security.exception;

/**
 * 密码错误异常
 *
 * @author bojiangzhou 2019/02/25
 */
public class ErrorWithTimesException extends CustomAuthenticationException {
    private static final long serialVersionUID = -3269204624220262800L;

    private final transient Object[] parameters;

    /**
     * 错误次数
     */
    private long errorTimes;

    /**
     * 剩余次数
     */
    private long surplusTimes;

    public ErrorWithTimesException(String msg, Throwable t, Object... parameters) {
        super(msg, t);
        this.parameters = parameters;
    }

    public ErrorWithTimesException(String msg, Object... parameters) {
        super(msg);
        this.parameters = parameters;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    public long getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(long errorTimes) {
        this.errorTimes = errorTimes;
    }

    public long getSurplusTimes() {
        return surplusTimes;
    }

    public void setSurplusTimes(long surplusTimes) {
        this.surplusTimes = surplusTimes;
    }
}
