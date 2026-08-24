package org.hzero.gateway.helper.util;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * 集合工具类
 * </p>
 *
 * @author qingsheng.chen 2019/3/15 星期五 14:14
 */
public class CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * 合并两个集合
     *
     * @param col1 集合1
     * @param col2 集合2
     * @param <T>  泛型
     * @return 合并结果 永不为null
     */
    public static <T> Collection<T> merge(Collection<T> col1, Collection<T> col2) {
        if (col1 == null && col2 == null) {
            return Collections.emptyList();
        } else if (col1 == null) {
            return col2;
        } else if (col2 == null) {
            return col1;
        } else {
            col1.addAll(col2);
            return col2;
        }
    }
}
