package io.choerodon.core.convertor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * spring工厂调用辅助类
 *
 * @author flyleft
 */
public class ApplicationContextHelper implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextHelper.class);

    private static DefaultListableBeanFactory springFactory;

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextHelper.setContext(applicationContext);
        if (applicationContext instanceof AbstractRefreshableApplicationContext) {
            AbstractRefreshableApplicationContext springContext =
                    (AbstractRefreshableApplicationContext) applicationContext;
            ApplicationContextHelper.setFactory((DefaultListableBeanFactory) springContext.getBeanFactory());
        } else if (applicationContext instanceof GenericApplicationContext) {
            GenericApplicationContext springContext = (GenericApplicationContext) applicationContext;
            ApplicationContextHelper.setFactory(springContext.getDefaultListableBeanFactory());
        }
    }

    private static void setContext(ApplicationContext applicationContext) {
        ApplicationContextHelper.context = applicationContext;
    }

    private static void setFactory(DefaultListableBeanFactory springFactory) {
        ApplicationContextHelper.springFactory = springFactory;
    }

    public static DefaultListableBeanFactory getSpringFactory() {
        return springFactory;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    /**
     * 异步从 ApplicationContextHelper 获取 bean 对象并设置到目标对象中，在某些启动期间需要初始化的bean可采用此方法。
     *
     * 适用于实例方法注入。
     *
     * @param type         bean type
     * @param target       目标类对象
     * @param setterMethod setter 方法，target 中需包含此方法名，且类型与 type 一致
     * @param <T>          type
     */
    public static <T> void asyncInstanceSetter(Class<T> type, Object target, String setterMethod) {
        if (setByMethod(type, target, setterMethod)) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "sync-setter"));
        executorService.scheduleAtFixedRate(() -> {
            boolean success = setByMethod(type, target, setterMethod);
            if (success) {
                executorService.shutdown();
            } else {
                if (counter.addAndGet(1) > 240) {
                    LOGGER.error("Setter field [{}] in [{}] failure because timeout.", setterMethod, target.getClass().getName());
                    executorService.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 异步从 ApplicationContextHelper 获取 bean 对象并设置到目标对象中，在某些启动期间需要初始化的bean可采用此方法。
     * <br>
     * 一般可用于向静态类注入实例对象。
     *
     * @param type         bean type
     * @param target       目标类
     * @param targetField  目标字段
     */
    public static void asyncStaticSetter(Class<?> type, Class<?> target, String targetField) {
        if (setByField(type, target, targetField)) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "sync-setter"));
        executorService.scheduleAtFixedRate(() -> {
            boolean success = setByField(type, target, targetField);
            if (success) {
                executorService.shutdown();
            } else {
                if (counter.addAndGet(1) > 240) {
                    LOGGER.error("Setter field [{}] in [{}] failure because timeout.", targetField, target.getName());
                    executorService.shutdown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static boolean setByMethod(Class<?> type, Object target, String targetMethod) {
        if (ApplicationContextHelper.getContext() != null) {
            try {
                Object obj = ApplicationContextHelper.getContext().getBean(type);
                Method method = target.getClass().getDeclaredMethod(targetMethod, type);
                method.setAccessible(true);
                method.invoke(target, obj);
                LOGGER.info("Async set field [{}] in [{}] success by method.", targetMethod, target.getClass().getName());
                return true;
            } catch (NoSuchMethodException e) {
                LOGGER.error("Not found method [{}] in [{}].", targetMethod, target.getClass().getName(), e);
            } catch (NoSuchBeanDefinitionException e) {
                LOGGER.error("Not found bean [{}] for [{}].", type.getName(), target.getClass().getName(), e);
            } catch (Exception e) {
                LOGGER.error("Async set field [{}] in [{}] failure by method.", targetMethod, target.getClass().getName(), e);
            }
        }
        return false;
    }

    private static boolean setByField(Class<?> type, Class<?> target, String targetField) {
        if (ApplicationContextHelper.getContext() != null) {
            try {
                Object obj = ApplicationContextHelper.getContext().getBean(type);
                Field field = target.getDeclaredField(targetField);
                field.setAccessible(true);
                field.set(target, obj);
                LOGGER.info("Async set field [{}] in [{}] success by field.", targetField, target.getName());
                return true;
            } catch (NoSuchFieldException e) {
                LOGGER.error("Not found field [{}] in [{}].", targetField, target.getName(), e);
            } catch (NoSuchBeanDefinitionException e) {
                LOGGER.error("Not found bean [{}] for [{}].", type.getName(), target.getName(), e);
            } catch (Exception e) {
                LOGGER.error("Async set field [{}] in [{}] failure by field.", targetField, target.getName(), e);
            }
        }
        return false;
    }


}
