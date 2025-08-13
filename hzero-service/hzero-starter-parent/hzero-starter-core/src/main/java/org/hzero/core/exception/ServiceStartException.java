package org.hzero.core.exception;

import io.choerodon.core.exception.CommonException;

/**
 * 自定义服务启动异常
 *
 * @author XCXCXCXCX
 * @date 2020/6/11 12:22 下午
 */
public class ServiceStartException extends CommonException {

    private String failureCause;

    public ServiceStartException(String code, String failureCause, Object... parameters) {
        super(code, parameters);
        this.failureCause = failureCause;
    }

    public ServiceStartException(String code, Throwable cause, String failureCause, Object... parameters) {
        super(code, cause, parameters);
        this.failureCause = failureCause;
    }

    public ServiceStartException(String code, Throwable cause, String failureCause) {
        super(code, cause);
        this.failureCause = failureCause;
    }

    public ServiceStartException(Throwable cause, String failureCause, Object... parameters) {
        super(cause, parameters);
        this.failureCause = failureCause;
    }

    public String getFailureCause() {
        return failureCause;
    }
}
