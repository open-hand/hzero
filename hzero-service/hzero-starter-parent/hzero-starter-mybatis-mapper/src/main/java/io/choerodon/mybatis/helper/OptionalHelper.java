package io.choerodon.mybatis.helper;

import java.util.Collections;
import java.util.List;

/**
 * Created by xausky on 4/10/17.
 */
public class OptionalHelper {
    private static ThreadLocal<List<String>> optionals = new ThreadLocal<>();

    private OptionalHelper() {

    }

    /**
     * 获取自定义查询、插入、更新列
     *
     * @return List
     */
    public static List<String> optional() {
        if (optionals.get() == null) {
            optionals.set(Collections.emptyList());
        }
        return optionals.get();
    }

    public static void optional(List<String> optional) {
        optionals.set(optional);
    }
}
