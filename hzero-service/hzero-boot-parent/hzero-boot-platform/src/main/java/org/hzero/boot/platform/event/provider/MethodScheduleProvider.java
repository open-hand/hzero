package org.hzero.boot.platform.event.provider;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hzero.boot.platform.event.Constants;
import org.hzero.boot.platform.event.handler.EventHandlerBean;
import org.hzero.boot.platform.event.handler.EventHandlerHolder;
import org.hzero.boot.platform.event.helper.AsyncScheduleHelper;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.vo.EventParam;
import org.hzero.boot.platform.event.vo.EventRuleVO;
import org.hzero.boot.platform.event.vo.MethodParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 方法提供器
 *
 * @author bergturing 2020/08/11 10:59
 */
@Component
public class MethodScheduleProvider extends AbstractScheduleProvider {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodScheduleProvider.class);

    private final LocalVariableTableParameterNameDiscoverer discoverer;

    /**
     * 事件Handler
     */
    private final EventHandlerHolder eventHandlerHolder;

    /**
     * 异步调用Helper
     */
    private final AsyncScheduleHelper asyncScheduleHelper;

    @Autowired
    public MethodScheduleProvider(RuleMatcher ruleMatcher,
                                  EventHandlerHolder eventHandlerHolder, AsyncScheduleHelper asyncScheduleHelper) {
        super(ruleMatcher);
        this.eventHandlerHolder = eventHandlerHolder;
        this.asyncScheduleHelper = asyncScheduleHelper;

        this.discoverer = new LocalVariableTableParameterNameDiscoverer();
    }

    @Override
    protected Object doSchedule(@Nonnull EventRuleVO eventRuleVO, EventParam eventParam) throws Exception {
        String beanName = eventRuleVO.getBeanName();
        String methodName = eventRuleVO.getMethodName();
        EventHandlerBean eventHandler = eventHandlerHolder.getEventHandlerBean(beanName);
        if (eventHandler == null) {
            LOGGER.error(">>>>> 没有找到事件处理对象[{}]", beanName);
            throw new NullPointerException("not find the event handler object");
        }
        Method eventHandlerMethod = eventHandlerHolder.getEventHandlerMethod(beanName, methodName);

        if (eventHandlerMethod == null) {
            LOGGER.error(">>>>> 没有找到事件处理方法[{}]", methodName);
            throw new NullPointerException("not find the event handler method");
        }
        eventHandlerMethod.setAccessible(true);
        Object[] methodArgs = this.getMethodArgs(eventHandlerMethod, eventParam);
        // 同步调用
        if (eventRuleVO.syncCall()) {
            LOGGER.debug(">>>>> 同步方法调用");
            Object resultTmp = eventHandlerMethod.invoke(eventHandler, methodArgs);
            if (eventRuleVO.enableResult()) {
                return resultTmp;
            }
        } else {
            // 异步调用
            LOGGER.debug(">>>>> 异步方法调用");
            asyncScheduleHelper.asyncMethodSchedule(eventHandler, eventHandlerMethod, methodArgs);
        }

        return null;
    }

    @Override
    public String supportType() {
        return Constants.CallType.METHOD;
    }

    private Object[] getMethodArgs(Method method, EventParam eventParam) {
        String[] parameterNames = Optional.ofNullable(this.discoverer.getParameterNames(method))
                .orElse(ArrayUtils.EMPTY_STRING_ARRAY);
        Object[] args = ArrayUtils.EMPTY_OBJECT_ARRAY;

        if (ArrayUtils.isNotEmpty(parameterNames)) {
            args = new Object[parameterNames.length];
            MethodParam methodParam = null;
            if (eventParam instanceof MethodParam) {
                methodParam = (MethodParam) eventParam;
            }
            if (MapUtils.isEmpty(methodParam)) {
                LOGGER.error(">>>>> 调用方法参数[{}]与MethodParam[{}]不匹配", parameterNames, methodParam);
                throw new IllegalArgumentException("调用方法参数与MethodParam不匹配");
            }
            for (int i = 0; i < parameterNames.length; i++) {
                args[i] = methodParam.get(parameterNames[i]);
            }
        }

        return args;
    }
}
