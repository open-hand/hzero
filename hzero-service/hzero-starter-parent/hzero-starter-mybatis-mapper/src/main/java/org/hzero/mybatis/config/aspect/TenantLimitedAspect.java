package org.hzero.mybatis.config.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hzero.mybatis.annotation.TenantLimitedRequest;
import org.hzero.mybatis.helper.TenantLimitedHelper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 租户限制的请求拦截
 * </p>
 *
 * @author qingsheng.chen 2019/2/28 星期四 10:02
 */
@Aspect
@Component
public class TenantLimitedAspect {

    @Around("@annotation(tenantLimitedRequest)")
    public Object tenantLimitedPointcut(ProceedingJoinPoint joinPoint, TenantLimitedRequest tenantLimitedRequest) throws Throwable {

        TenantLimitedHelper.open(tenantLimitedRequest.equal());
        return joinPoint.proceed();
    }
}
