package org.hzero.lock.enums;

/**
 * 
 * 锁类型
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午4:07:09
 */
public enum LockType {
    /**
     * 可重入锁
     */
    REENTRANT,
    /**
     * 公平锁
     */
    FAIR,
    /**
     * 联锁
     */
    MULTI,
    /**
     * 红锁
     */
    RED,
    /**
     * 读锁
     */
    READ,
    /**
     * 写锁
     */
    WRITE;

    LockType() {}

}
