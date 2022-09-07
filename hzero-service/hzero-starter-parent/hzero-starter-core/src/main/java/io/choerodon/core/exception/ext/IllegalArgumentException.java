package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 不合法参数异常
 *
 * @author superlee
 * @since 2019-07-11
 */
public class IllegalArgumentException extends CommonException {
    private static final long serialVersionUID = 8147033315680016528L;

    public IllegalArgumentException(String code, Object... parameters) {
        super(code, parameters);
    }

    public IllegalArgumentException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public IllegalArgumentException(String code, Throwable cause) {
        super(code, cause);
    }

    public IllegalArgumentException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
