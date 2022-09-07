package org.hzero.core.util;

import java.lang.reflect.Field;

/**
 * 反射工具类
 *
 * @author XCXCXCXCX
 * @see Reflections
 * @deprecated 方法移到 {@link Reflections}
 */
@Deprecated
public class ReflectionUtils {

    public static Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (target == null) {
            return null;
        }
        Class clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }

    public static <T> T getFieldValue(Object target, String fieldName, Class<T> returnType) throws NoSuchFieldException, IllegalAccessException {
        if (target == null) {
            return null;
        }
        Class clazz = target.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }
}
