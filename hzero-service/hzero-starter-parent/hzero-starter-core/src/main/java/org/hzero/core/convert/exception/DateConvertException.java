package org.hzero.core.convert.exception;

/**
 * <p>
 * 日期转换异常
 * </p>
 *
 * @author qingsheng.chen 2018/8/20 星期一 19:44
 */
public class DateConvertException extends RuntimeException {

    private static final long serialVersionUID = -847651565156777077L;

    public DateConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
