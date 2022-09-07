package org.hzero.core.redis.safe;

/**
 * 更新操作
 *
 * @author bojiangzhou 2019/07/23
 */
@FunctionalInterface
public interface ExecuteWithResult<T> {

    T get();

}
