package org.hzero.core.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 处理 {@link CacheValue}
 *
 * @author bojiangzhou 2018/08/16
 * @see CacheValue
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessCacheValue {

}
