package org.hzero.boot.platform.event.handler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.ProxyUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 自动扫描实现了 {@link EventHandlerBean} 的事件处理类。<br/>
 * 使用Map存储事件处理类及处理方法。
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 16:39
 */
public class EventHandlerHolder implements CommandLineRunner {

    private final Map<String, EventHandlerBean> handlerHolder = new ConcurrentHashMap<>();
    private final Map<String, Method> handlerMethodHolder = new ConcurrentHashMap<>();

    /**
     * @param beanName 事件处理类名称
     * @return 事件处理对象
     */
    public EventHandlerBean getEventHandlerBean(String beanName) {
        return handlerHolder.get(beanName);
    }

    /**
     * @param beanName   事件处理类名称
     * @param methodName 事件处理方法名称
     * @return 事件处理方法
     */
    public Method getEventHandlerMethod(String beanName, String methodName) {
        return handlerMethodHolder.get(beanName + "." + methodName);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, EventHandlerBean> beans = ApplicationContextHelper.getContext().getBeansOfType(EventHandlerBean.class);
        beans.forEach((k, handlerBean) -> {
            String beanName = StringUtils.isNotEmpty(handlerBean.getBeanName()) ? handlerBean.getBeanName() : k;
            handlerHolder.put(beanName, handlerBean);

            Method[] methods = ProxyUtils.getUserClass(handlerBean).getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(EventHandlerMethod.class)) {
                    EventHandlerMethod handlerMethodAnnotation = method.getAnnotation(EventHandlerMethod.class);
                    String methodName = StringUtils.isNotBlank(handlerMethodAnnotation.name())
                            ? handlerMethodAnnotation.name()
                            : method.getName();
                    handlerMethodHolder.put(beanName + "." + methodName, method);
                }
            }
        });
    }
}
