package org.hzero.boot.platform.event.helper;

import java.lang.reflect.Method;

import org.hzero.boot.platform.event.handler.EventHandlerBean;
import org.hzero.boot.platform.event.vo.ApiParam;
import org.springframework.http.HttpMethod;

/**
 * 异步线程调度辅助类
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 21:48
 */
public interface AsyncScheduleHelper {

    /**
     * 异步API调度
     * 
     * @param url API地址
     * @param method API方法 {@link HttpMethod}
     * @param apiParam API接口参数 {@link ApiParam}
     * @param requestHelper Http请求辅助类 {@link RequestHelper}
     */
    void asyncApiSchedule(String url, HttpMethod method, ApiParam apiParam, RequestHelper requestHelper);

    /**
     * 异步方法调度
     *
     * @param eventHandler 事件处理类 {@link EventHandlerBean}
     * @param eventHandlerMethod 事件处理方法 {@link Method}
     * @param args 参数
     */
    void asyncMethodSchedule(EventHandlerBean eventHandler, Method eventHandlerMethod, Object... args);
}
