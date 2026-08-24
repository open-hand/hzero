package org.hzero.lock.service;

import org.hzero.lock.LockInfo;

/**
 *
 * 锁服务接口
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午7:03:24
 */
public interface LockService {

    /**
     * 添加锁信息
     * @param lockInfo 锁信息
     */
    void setLockInfo(LockInfo lockInfo);

    /**
     * 获取锁信息
     *
     */
    LockInfo getLockInfo();

    /**
     * 清除锁信息
     *
     */
    void clearLockInfo();

    /**
     * 加锁
     */
    boolean lock();

    /**
     *
     * 释放锁
     */
    void releaseLock();
}
