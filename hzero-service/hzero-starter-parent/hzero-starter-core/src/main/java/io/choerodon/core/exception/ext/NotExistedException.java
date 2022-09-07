package io.choerodon.core.exception.ext;

import io.choerodon.core.exception.CommonException;

/**
 * 对象不存在异常
 *
 * @author superlee
 * @since 2019-07-10
 */
public class NotExistedException extends CommonException {
    private static final long serialVersionUID = -564598100169325016L;

    public NotExistedException(String code, Object... parameters) {
        super(code, parameters);
    }

    public NotExistedException(String code, Throwable cause, Object... parameters) {
        super(code, cause, parameters);
    }

    public NotExistedException(String code, Throwable cause) {
        super(code, cause);
    }

    public NotExistedException(Throwable cause, Object... parameters) {
        super(cause, parameters);
    }
}
