package io.choerodon.mybatis;

/**
 * 通用mapper异常类
 *
 * @author liuzh
 */
public class MapperException extends RuntimeException {
    public MapperException() {
        super();
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }

}
