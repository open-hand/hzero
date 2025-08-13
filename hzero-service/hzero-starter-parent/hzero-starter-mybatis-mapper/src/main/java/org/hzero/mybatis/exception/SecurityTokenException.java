package org.hzero.mybatis.exception;

/**
 * <p>
 * 数据安全异常
 * </p>
 *
 * @author qingsheng.chen 2018/9/10 星期一 11:50
 */
public class SecurityTokenException extends RuntimeException {

    public SecurityTokenException(String message) {
        super(message);
    }
}
