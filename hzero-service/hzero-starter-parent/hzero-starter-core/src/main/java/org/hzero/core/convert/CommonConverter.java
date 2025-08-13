package org.hzero.core.convert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 通用对象转换工具
 *
 * @author min.wang01@hand-china.com 2018/08/08 11:52
 */
public class CommonConverter {
    /**
     * List对象转换
     *
     * @param targetClazz 目标对象class
     * @param sourceList  源对象list
     * @param <T>         目标对象
     * @param <V>         源对象
     * @return 目标对象 List
     */
    public static <T, V> List<T> listConverter(Class<T> targetClazz, List<V> sourceList) {
        return listConverter(targetClazz, sourceList, null);
    }

    /**
     * List对象转换
     *
     * @param targetClazz 目标对象class
     * @param sourceList  源对象list
     * @param rule        自定义映射规则(target字段,source字段)
     * @param <T>         目标对象
     * @param <V>         源对象
     * @return 目标对象 List
     */
    public static <T, V> List<T> listConverter(Class<T> targetClazz, List<V> sourceList, Map<String, String> rule) {
        Assert.notNull(sourceList, "source convert list can't be null.");
        List<T> targetObjects = new ArrayList<>();
        sourceList.forEach(sourceObject -> {
            try {
                source2Target(targetClazz, rule, targetObjects, sourceObject);
            } catch (Exception e) {
                throw new CommonException("convert to {} error:", e, targetClazz.getName());
            }
        });
        return targetObjects;
    }

    private static <T, V> void source2Target(Class<T> targetClazz, Map<String, String> rule, List<T> targetObjects, V sourceObject) throws InstantiationException, IllegalAccessException {
        T target = targetClazz.newInstance();
        BeanUtils.copyProperties(sourceObject, target);
        // 复制自定义映射
        if (rule != null) {
            rule.forEach((targetField, sourceField) -> {
                try {
                    Field sourceObjField = sourceObject.getClass().getDeclaredField(sourceField);
                    sourceObjField.setAccessible(true);
                    Object value = sourceObjField.get(sourceObject);
                    Field targetObjField = target.getClass().getDeclaredField(targetField);
                    targetObjField.setAccessible(true);
                    targetObjField.set(target, value);
                } catch (Exception e) {
                    throw new CommonException("convert field error:", e, targetField);
                }
            });
        }
        targetObjects.add(target);
    }

    /**
     * List对象转换(过滤当前多语言)
     *
     * @param targetClazz 目标对象class
     * @param field       多语言字段名
     * @param sourceList  源对象list
     * @param <T>         目标对象
     * @param <V>         源对象
     * @return 目标对象 List
     */
    public static <T, V> List<T> listConverter(Class<T> targetClazz, String field, List<V> sourceList) {
        return listConverter(targetClazz, field, sourceList, null);
    }

    /**
     * description
     *
     * @param targetClazz 目标对象class
     * @param field       多语言字段名
     * @param sourceList  源对象list
     * @param rule        自定义映射规则(target字段,source字段)
     * @param <T>         目标对象
     * @param <V>         源对象
     * @return 目标对象 List
     */
    public static <T, V> List<T> listConverter(Class<T> targetClazz, String field, List<V> sourceList,
                                               Map<String, String> rule) {
        Assert.notNull(sourceList, "source convert list can't be null.");
        List<T> targetObjects = new ArrayList<>();
        sourceList.forEach(sourceObject -> {
            try {
                Field langField = sourceObject.getClass().getDeclaredField(field);
                langField.setAccessible(true);
                if (!Objects.equals(DetailsHelper.getUserDetails().getLanguage(), langField.get(sourceObject))) {
                    source2Target(targetClazz, rule, targetObjects, sourceObject);
                }
            } catch (Exception e) {
                throw new CommonException("convert to {} error:", e, targetClazz.getName());
            }
        });
        return targetObjects;
    }


    /**
     * javaBean对象转换
     *
     * @param targetClazz  目标对象class
     * @param sourceObject 源对象
     * @param <T>          目标对象
     * @param <V>          源对象
     * @return 目标对象
     */
    public static <T, V> T beanConvert(Class<T> targetClazz, V sourceObject) {
        Assert.notNull(sourceObject, "source convert object can't be null.");
        try {
            T targetObject = targetClazz.newInstance();
            BeanUtils.copyProperties(sourceObject, targetObject);
            return targetObject;
        } catch (Exception e) {
            throw new CommonException("convert to {} error:", e, targetClazz.getName());
        }
    }
}
