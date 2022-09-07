package org.hzero.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Objects;

/**
 * 锁基本信息
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午4:06:52
 */
public class LockInfo {

    private String name;
    private long waitTime;
    private long leaseTime;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private List<String> keyList;

    public LockInfo() {
    }

    public LockInfo(String name, long waitTime, long leaseTime, TimeUnit timeUnit) {
        this.name = name;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
        this.timeUnit = timeUnit;
    }

    public LockInfo(String name, List<String> keyList, long waitTime, long leaseTime, TimeUnit timeUnit) {
        this.name = name;
        this.keyList = keyList;
        this.waitTime = waitTime;
        this.leaseTime = leaseTime;
        this.timeUnit = timeUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public void setKeyList(List<String> keyList) {
        this.keyList = keyList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LockInfo lockInfo = (LockInfo) o;
        return waitTime == lockInfo.waitTime &&
                leaseTime == lockInfo.leaseTime &&
                Objects.equal(name, lockInfo.name) &&
                timeUnit == lockInfo.timeUnit &&
                Objects.equal(keyList, lockInfo.keyList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, waitTime, leaseTime, timeUnit, keyList);
    }
}
