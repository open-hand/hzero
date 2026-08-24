package org.hzero.core.exception;

import org.hzero.core.base.BaseConstants;

/**
 * 乐观锁更新异常
 *
 * @author bojiangzhou 2018/07/01
 */
public class OptimisticLockException extends RuntimeException {
    private static final long serialVersionUID = -4289111887481382553L;

    public OptimisticLockException() {
        super(BaseConstants.ErrorCode.OPTIMISTIC_LOCK);
    }

}
