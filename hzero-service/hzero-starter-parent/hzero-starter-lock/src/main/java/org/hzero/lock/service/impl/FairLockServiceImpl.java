package org.hzero.lock.service.impl;

import org.hzero.lock.LockInfo;
import org.hzero.lock.service.LockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 公平锁实现
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午4:28:07
 */
public class FairLockServiceImpl implements LockService {

    @Qualifier("lockRedissonClient")
    @Autowired
    private RedissonClient redissonClient;

    private final ThreadLocal<LockInfo> lockInfoThreadLocal = new ThreadLocal<>();

    @Override
    public void setLockInfo(LockInfo lockInfo) {
        lockInfoThreadLocal.set(lockInfo);
    }

    @Override
    public LockInfo getLockInfo() {
        return lockInfoThreadLocal.get();
    }

    @Override
    public void clearLockInfo() {
        lockInfoThreadLocal.remove();
    }

    @Override
    public boolean lock() {
        LockInfo lockInfo = lockInfoThreadLocal.get();
        RLock rLock = redissonClient.getFairLock(lockInfo.getName());
        try {
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), lockInfo.getTimeUnit());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void releaseLock() {
        LockInfo lockInfo = lockInfoThreadLocal.get();
        RLock rLock = redissonClient.getFairLock(lockInfo.getName());
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlockAsync();
        }
        lockInfoThreadLocal.remove();
    }
}
