package org.hzero.core.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author bojiangzhou 2020/02/24
 */
public class CommonStream {

    /**
     * 根据某个字段去重
     *
     * @param keyExtractor 获取值
     * @param <T>          去重对象
     * @return Predicate
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
