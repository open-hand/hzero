package org.hzero.feign.aspect;

import java.lang.reflect.Type;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * 拦截 {@link FeignClient} 的接口调用，获取调用的服务名称
 *
 * @author bojiangzhou 2018/09/28
 */
@Aspect
public class FeignClientAspect {

    @Pointcut("execution(* *..*.infra.feign.*.*(..))")
    private void feignClient() {

    }

    /**
     * 拦截 *Feign 结尾所有注解 {@link FeignClient} 的所有方法 这里无法直接通过注解方式拦截 @FeignClient 注解的接口，因为
     * FeignClient 只有接口，没有实现(生成的是代理类)
     */
    @Before(value = "feignClient()")
    public void keepServiceName(JoinPoint joinPoint) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }
        Type type = joinPoint.getTarget().getClass().getGenericInterfaces()[0];
        FeignClient annotation = AnnotationUtils.findAnnotation((Class) type, FeignClient.class);
        if (annotation != null) {
            // 将服务名放入ThreadLocal中
            String serviceName = annotation.name();
            FeignVariableHolder.FEIGN_SERVICE_NAME.set(StringUtils.lowerCase(serviceName));
        }
    }

    @After(value = "feignClient()")
    public void removeServiceName(JoinPoint joinPoint) {
        // 移除变量，避免内存泄露
        FeignVariableHolder.FEIGN_SERVICE_NAME.remove();
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }
}
