package io.choerodon.core.convertor;

import io.choerodon.core.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 类型转换辅助类
 *
 * @author flyleft
 * 2018/3/16
 */
public class ConvertHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertHelper.class);

    private static final Map<String, DestinClassData> CONVERT_MAP = new HashMap<>();

    static final String SPRING_PROXY_CLASS = "EnhancerBySpringCGLIB";

    private ConvertHelper() {
    }

    /**
     * list的转换
     *
     * @param listSource 要转换的list集合
     * @param destin     要转换的目标类型的Class
     * @param <T>        要转换的目标类型
     * @return 转换的后list集合
     */
    public static <T> List<T> convertList(final List listSource, Class<T> destin) {
        if (listSource == null) {
            return Collections.emptyList();
        }
        if (listSource.isEmpty()) {
            return new ArrayList<>(0);
        }
        Class<?> source = listSource.get(0).getClass();
        if (source.getTypeName().contains(SPRING_PROXY_CLASS)) {
            source = source.getSuperclass();
        }
        final DestinClassData destinClassData = getDestinClassData(source, destin);
        List<T> list = new ArrayList<>(listSource.size());
        for (Object object : listSource) {
            T t = invokeConvert(destinClassData, object);
            list.add(t);
        }
        return list;
    }

    /**
     * 单个对象的类型转换
     *
     * @param obj    要转换的对象
     * @param destin 要转换的类型CLass
     * @param <T>    要转换的类型
     * @return 转换的后的对象
     */
    public static <T> T convert(final Object obj, Class<T> destin) {
        if (obj == null) {
            return null;
        }
        Class<?> source = obj.getClass();
        if (source.getTypeName().contains(SPRING_PROXY_CLASS)) {
            source = source.getSuperclass();
        }
        DestinClassData destinClassData = getDestinClassData(source, destin);
        return invokeConvert(destinClassData, obj);
    }

    @SuppressWarnings("unchecked")
    static <T> T invokeConvert(final DestinClassData destinClassData, final Object obj) {
        T t = null;
        try {
            destinClassData.method.setAccessible(true);
            Object result = destinClassData.method.invoke(destinClassData.convertorI, obj);
            if (result != null) {
                t = (T) result;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.warn("error.ConvertHelper.invokeConvert {}", e.toString());
        }
        return t;
    }

    static DestinClassData getDestinClassData(final Class source, final Class destin) {
        String key = source.getTypeName() + destin.getTypeName();
        //1. 获取要使用的Convertor对象。
        DestinClassData destinClassData = CONVERT_MAP.get(key);
        if (destinClassData == null) {
            destinClassData = new DestinClassData();
            CONVERT_MAP.put(key, destinClassData);
        }

        if (destinClassData.convertorClass == null) {
            destinClassData.convertorClass = getConvertClass(source, destin);
        }

        //2. 获取要调用的covert对象实例。

        if (destinClassData.convertorI == null) {
            destinClassData.convertorI = ApplicationContextHelper.getSpringFactory().getBean(destinClassData.convertorClass);
        }

        //3. 获取真正要调用的方法。
        Method method = destinClassData.method;
        if (method == null) {
            destinClassData.method = getMethod(source, destin, destinClassData.convertorClass);
        }
        return destinClassData;
    }

    private static Method getMethod(final Class source, final Class destin, final Class<? extends ConvertorI> convertorClass) {
        Method[] methods = convertorClass.getDeclaredMethods();
        for (Method method : methods) {
            Class[] paramTypes = method.getParameterTypes();
            if (method.getReturnType().equals(destin)
                    && paramTypes.length == 1
                    && paramTypes[0].equals(source)) {
                return method;
            }
        }
        throw new CommonException("error.convertHelper.getMethod, sourceClass: "
                + source.getName() + " destinClass: " + destin.getName());
    }

    private static Class<? extends ConvertorI> getConvertClass(final Class source, final Class destin) {
        Map<String, ConvertorI> convertorIMap = ApplicationContextHelper.getSpringFactory()
                .getBeansOfType(ConvertorI.class);
        for (ConvertorI i : convertorIMap.values()) {
            Type[] interfacesTypes = i.getClass().getGenericInterfaces();
            for (Type t : interfacesTypes) {
                if (t instanceof ParameterizedType && isConvertorI(t, source, destin)) {
                    return i.getClass();
                }
            }
        }
        throw new CommonException("error.convertHelper.getConvertClass, sourceClass: "
                + source.getName() + " destinClass: " + destin.getName());
    }

    private static boolean isConvertorI(final Type t, final Class source, final Class destin) {
        Type rawType = ((ParameterizedType) t).getRawType();
        if (rawType == null ) {
            return false;
        }
        if (!ConvertorI.class.getTypeName().equals(rawType.getTypeName())) {
            Type[] superInterfaces = ((Class) rawType).getGenericInterfaces();
            for (Type type : superInterfaces) {
                if (!ConvertorI.class.getTypeName().equals(((ParameterizedType) type).getRawType().getTypeName())) {
                    return false;
                }
            }
        }
        Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
        int num = 0;
        for (Type t2 : genericType2) {
            if (source.getTypeName().equals(t2.getTypeName())
                    || destin.getTypeName().equals(t2.getTypeName())) {
                num++;
            }
        }
        return num > 1;
    }


    static class DestinClassData {

        private Class<? extends ConvertorI> convertorClass;

        private Method method;

        private ConvertorI convertorI;

    }

}
