package org.hzero.core.jackson.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 过滤字符串两端空格
 * </p>
 *
 * @author qingsheng.chen 2018/10/16 星期二 10:45
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Trim {

    TrimMode mode() default TrimMode.ALL;

    enum TrimMode {
        /**
         * 左侧空格
         */
        LEFT,
        /**
         * 右侧空格
         */
        RIGHT,
        /**
         * 两侧空格
         */
        ALL
    }
}
