package org.hzero.core.jackson.sensitive;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.core.jackson.cache.SensitiveCache;
import org.hzero.core.util.SensitiveUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 敏感信息设置类
 * </p>
 *
 * @author qingsheng.chen 2018/9/18 星期二 10:31
 */
public class SensitiveHelper {
    private SensitiveHelper() {
    }

    private static ThreadLocal<Boolean> sensitiveSwitch = new ThreadLocal<>();

    /**
     * 开启状态下，使用一次之后关闭
     */
    public static void open() {
        sensitiveSwitch.set(true);
    }

    public static void close() {
        sensitiveSwitch.set(false);
    }

    public static boolean isOpen() {
        return BooleanUtils.isTrue(sensitiveSwitch.get());
    }

    public static void remove() {
        sensitiveSwitch.remove();
    }

    /**
     * 校验是否包含敏感信息
     *
     * @param value 值
     * @return 是否包含敏感信息
     */
    public static boolean hasSensitive(String value, Class<?> entityClass, String fieldName) {
        return StringUtils.hasText(value) && !SensitiveUtils.generateCipherText(SensitiveCache.get(entityClass, fieldName), value).equals(value);
    }
}
