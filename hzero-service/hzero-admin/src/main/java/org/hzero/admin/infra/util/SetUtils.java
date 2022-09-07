package org.hzero.admin.infra.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 10:47 上午
 */
public class SetUtils {


    /**
     * 求两个集合的交集
     * @param s1
     * @param s2
     * @param <T>
     * @return
     */
    public static <T> Set<T> getIntersection(Collection<T> s1, Collection<T> s2) {
        Set<T> set1 = new HashSet<>(s1);
        Set<T> set2 = new HashSet<>(s2);
        set1.retainAll(set2);
        return set1;
    }
}
