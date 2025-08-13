package org.hzero.core.jackson.cache;

import org.hzero.core.jackson.annotation.Sensitive;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 敏感信息缓存
 * </p>
 *
 * @author qingsheng.chen 2018/9/18 星期二 10:48
 */
public class SensitiveCache {
    private static final String SPLIT = "@";
    private static final Map<String, Sensitive> sensitiveCacheMap = new HashMap<>();

    private SensitiveCache() {
    }

    public static void put(Class<?> entity, String fieldName, Sensitive sensitive) {
        String key = generateKey(entity, fieldName);
        if (containKey(key)) {
            return;
        }
        sensitiveCacheMap.put(key, sensitive);
    }

    public static Sensitive get(Class<?> entity, String fieldName) {
        return sensitiveCacheMap.get(generateKey(entity, fieldName));
    }

    public static boolean containKey(String key) {
        return sensitiveCacheMap.containsKey(key);
    }

    public static boolean containKey(Class<?> entity, String fieldName) {
        return sensitiveCacheMap.containsKey(generateKey(entity, fieldName));
    }

    public static String generateKey(Class<?> entity, String fieldName) {
        if (entity == null) {
            return fieldName;
        }
        return entity.getName() + SPLIT + fieldName;
    }
}
