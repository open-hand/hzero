package org.hzero.core.exception;

import org.hzero.core.base.BaseConstants;

/**
 * 未登录异常
 *
 * @author bojiangzhou 2018/07/03
 */
public class NotLoginException extends RuntimeException {
    private static final long serialVersionUID = -7084125757017246513L;

    public NotLoginException() {
        super(BaseConstants.ErrorCode.NOT_LOGIN);
    }

}
