package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 对象已存在异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class AlreadyExistedException extends CommonException {
    private static final long serialVersionUID = 3910700132720089286L;

    public AlreadyExistedException(String code, Object... parameters) {
        super(code, parameters);
    }

    public AlreadyExistedException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public AlreadyExistedException(String code, Throwable cause) {
        super(code, cause);
    }

    public AlreadyExistedException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
