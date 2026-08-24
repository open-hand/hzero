package org.hzero.lock.service.impl;

import org.hzero.lock.LockInfo;
import org.hzero.lock.service.LockService;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 红锁实现类
 *
 * @author xianzhi.chen@hand-china.com 2019年3月15日下午3:33:16
 */
public class RedLockServiceImpl implements LockService {

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
        RLock[] lockList = new RLock[lockInfo.getKeyList().size()];
        for (int i = 0; i < lockInfo.getKeyList().size(); i++) {
            lockList[i] = redissonClient.getLock(lockInfo.getKeyList().get(i));
        }
        try {
            RedissonRedLock lock = new RedissonRedLock(lockList);
            return lock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), lockInfo.getTimeUnit());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void releaseLock() {
        LockInfo lockInfo = lockInfoThreadLocal.get();
        RLock[] lockList = new RLock[lockInfo.getKeyList().size()];
        for (int i = 0; i < lockInfo.getKeyList().size(); i++) {
            lockList[i] = redissonClient.getLock(lockInfo.getKeyList().get(i));
        }
        RedissonRedLock lock = new RedissonRedLock(lockList);
        lock.unlock();
        lockInfoThreadLocal.remove();
    }
}
