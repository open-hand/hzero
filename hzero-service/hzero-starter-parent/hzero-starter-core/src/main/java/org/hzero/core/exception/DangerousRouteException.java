package org.hzero.core.exception;

import io.choerodon.core.exception.CommonException;

/**
 * 检测危险路由异常
 * @author XCXCXCXCX
 */
public class DangerousRouteException extends CommonException {

    public DangerousRouteException(String code, Object... parameters) {
        super(code, parameters);
    }

    public DangerousRouteException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public DangerousRouteException(String code, Throwable cause) {
        super(code, cause);
    }

    public DangerousRouteException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}