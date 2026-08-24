package org.hzero.core.util;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 反射工具类.
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/11 23:22
 */
@SuppressWarnings("rawtypes")
public class Reflections {

    private Reflections() {
    }

    private static final Logger logger = LoggerFactory.getLogger(Reflections.class);

    private static final Map<Class<?>, Object> DEFAULT_VALUES = Stream
            .of(boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class)
            .collect(toMap(clazz -> (Class<?>) clazz, clazz -> Array.get(Array.newInstance(clazz, 1), 0)));

    /**
     * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处. 如无法找到, 返回Object.class.
     *
     * @param clazz class类
     * @return 返回第一个声明的泛型类型. 如果没有,则返回Object.class
     */
    public static Class getClassGenericType(final Class clazz) {
        return getClassGenericType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *
     * @param clazz class类
     * @param index 获取第几个泛型参数的类型,默认从0开始,即第一个
     * @return 返回第index个泛型参数类型.
     */
    public static Class getClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (genType instanceof Class) {
            // try to climb up the hierarchy until meet something useful
            if (ParameterizedType.class != genType) {
                return getClassGenericType(clazz.getSuperclass(), index);
            }
        }

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            logger.warn("Index: {}, Size of {}'s Parameterized Type: {}", index, clazz.getSimpleName(), params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            logger.warn("{} not set the actual class on superclass generic parameter", clazz.getSimpleName());
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 获取实体的字段
     *
     * @param entityClass 实体类型
     * @param fieldName   字段名称
     * @return 该字段名称对应的字段, 如果没有则返回null.
     */
    public static Field getField(Class entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            makeAccessible(field);
            return field;
        } catch (NoSuchFieldException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 改变private/protected的成员变量为public.
     *
     * @param field Field
     */
    public static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * 获取Class的所有字段，以及父类字段
     */
    public static Field[] getAllField(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        Field[] result = new Field[fieldList.size()];
        return fieldList.toArray(result);
    }

    /**
     * 获取字段的值
     */
    public static Object getFieldValue(Object target, String fieldName) {
        if (target == null) {
            return null;
        }
        Class<?> clazz = target.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反射获取字段值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName, Class<T> returnType) {
        if (target == null) {
            return null;
        }
        Class clazz = target.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Java 基本类型默认值
     *
     * @param clazz 基本类型 Class
     * @param <T>   基本类型
     * @return 默认值
     */
    public static <T> T getDefaultValue(Class<T> clazz) {
        return (T) DEFAULT_VALUES.get(clazz);
    }
}
