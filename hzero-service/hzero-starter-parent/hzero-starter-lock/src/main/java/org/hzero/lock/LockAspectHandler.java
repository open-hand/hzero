package org.hzero.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hzero.lock.annotation.Lock;
import org.hzero.lock.factory.LockServiceFactory;
import org.hzero.lock.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * 切面加锁处理
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午4:52:06
 */
@Aspect
@Component
@Order(0)
public class LockAspectHandler {

    @Autowired
    private LockInfoProvider lockInfoProvider;
    @Autowired
    private LockServiceFactory lockFactory;


    @Around(value = "@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        // 获取锁信息
        LockInfo lockInfo = lockInfoProvider.getLockInfo(joinPoint, lock);
        // 获取锁服务
        LockService lockService = lockFactory.getLock(lock.lockType());
        // 若当前线程已经存在锁，不再添加新的锁
        if (lockService.getLockInfo() != null && !lockInfo.equals(lockService.getLockInfo())) {
            return joinPoint.proceed();
        }
        // 设置锁信息
        lockService.setLockInfo(lockInfo);
        boolean lockFlag = false;
        // 加锁
        try {
            boolean lockRes = lockService.lock();
            if (lockRes) {
                // 加锁成功
                lockFlag = true;
                return joinPoint.proceed();
            } else {
                throw new CommonException("Get lock failed.");
            }
        } finally {
            if (lockFlag) {
                lockService.releaseLock();
                lockService.clearLockInfo();
            }
        }
    }
}