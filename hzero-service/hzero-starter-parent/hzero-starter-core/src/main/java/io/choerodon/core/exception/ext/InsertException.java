package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 插入数据异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class InsertException extends CommonException {
    private static final long serialVersionUID = -7206816136427460064L;

    public InsertException(String code, Object... parameters) {
        super(code, parameters);
    }

    public InsertException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public InsertException(String code, Throwable cause) {
        super(code, cause);
    }

    public InsertException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
