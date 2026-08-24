package org.hzero.boot.platform.event.helper.impl;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hzero.boot.platform.event.handler.EventHandlerBean;
import org.hzero.boot.platform.event.helper.AsyncScheduleHelper;
import org.hzero.boot.platform.event.helper.RequestHelper;
import org.hzero.boot.platform.event.vo.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * 异步线程调度辅助类
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/13 21:48
 */
public class DefaultAsyncScheduleHelper implements AsyncScheduleHelper {

    private static final Logger LOOGER = LoggerFactory.getLogger(DefaultAsyncScheduleHelper.class);

    private ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    public void init() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("event-scheduler-%d").build();
        this.threadPoolExecutor = new ThreadPoolExecutor(32, 64, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
                        threadFactory);
    }

    @Override
    public void asyncApiSchedule(String url, HttpMethod method, ApiParam apiParam, RequestHelper requestHelper) {
        threadPoolExecutor.execute(new ApiScheduleRunnable(url, method, apiParam, requestHelper));
    }

    @Override
    public void asyncMethodSchedule(EventHandlerBean eventHandler, Method eventHandlerMethod, Object... args) {
        threadPoolExecutor.execute(new MethodScheduleRunnable(eventHandler, eventHandlerMethod, args));
    }

    /**
     * 方法调度线程
     */
    static class MethodScheduleRunnable implements Runnable {
        private EventHandlerBean eventHandler;
        private Method eventHandlerMethod;
        private Object[] args;

        MethodScheduleRunnable(EventHandlerBean eventHandler, Method eventHandlerMethod, Object[] args) {
            this.eventHandler = eventHandler;
            this.eventHandlerMethod = eventHandlerMethod;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                eventHandlerMethod.invoke(eventHandler, args);
            } catch (Exception e) {
                LOOGER.error(">>>>> 方法调用出现异常", e);
            }
        }
    }

    /**
     * API调度线程
     */
    static class ApiScheduleRunnable implements Runnable {
        private String url;
        private HttpMethod httpMethod;
        private ApiParam apiParam;
        private RequestHelper requestHelper;

        ApiScheduleRunnable(String url, HttpMethod httpMethod, ApiParam apiParam, RequestHelper requestHelper) {
            this.url = url;
            this.httpMethod = httpMethod;
            this.apiParam = apiParam;
            this.requestHelper = requestHelper;
        }

        @Override
        public void run() {
            try {
                requestHelper.request(url, httpMethod, apiParam);
            } catch (Exception e) {
                LOOGER.error(">>>>> API调用出现异常", e);
            }
        }
    }

}
