package org.hzero.mybatis.helper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.mybatis.annotation.CrossSchema;
import org.hzero.mybatis.security.CrossSchemaInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 跨库 Helper
 * </p>
 *
 * @author qingsheng.chen 2018/9/20 星期四 15:51
 */
public class CrossSchemaHelper {
    private CrossSchemaHelper() {
    }

    private static final String SPLIT = ".";
    private static final Logger logger = LoggerFactory.getLogger(CrossSchemaHelper.class);
    private static Map<String, CrossSchema> mapper = new HashMap<>();

    public static void close() {
        CrossSchemaInterceptor.CROSS_ENABLE.set(false);
    }

    public static void open() {
        CrossSchemaInterceptor.CROSS_ENABLE.set(true);
    }

    public static void clear() {
        CrossSchemaInterceptor.CROSS_ENABLE.remove();
    }

    public static CrossSchema crossSchema(String id) {
        if (!mapper.containsKey(id)) {
            initMapper(id);
        }
        return mapper.get(id);
    }

    public static void initMapper(String id) {
        mapper.put(id, getCrossSchema(id));
    }

    private static CrossSchema getCrossSchema(String id) {
        try {
            if (StringUtils.hasText(id) && id.contains(SPLIT)) {
                Class entityClass = ClassLoader.getSystemClassLoader().loadClass(getFullClassName(id));
                for (Method method : entityClass.getMethods()) {
                    if (method.getName().equals(getMethodName(id)) && method.isAnnotationPresent(CrossSchema.class)) {
                        return method.getAnnotation(CrossSchema.class);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error("Error load class when cross schema {}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private static String getMethodName(String id) {
        return id.substring(id.lastIndexOf(SPLIT) + 1);
    }

    private static String getFullClassName(String id) {
        return id.substring(0, id.lastIndexOf(SPLIT));
    }
}
