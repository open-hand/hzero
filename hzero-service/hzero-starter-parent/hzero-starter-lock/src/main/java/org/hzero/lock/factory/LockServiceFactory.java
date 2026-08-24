package org.hzero.lock.factory;

import java.util.EnumMap;

import org.hzero.lock.enums.LockType;
import org.hzero.lock.service.LockService;
import org.hzero.lock.service.impl.*;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 锁处理工厂类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月14日下午7:04:13
 */
public class LockServiceFactory {

    private static final EnumMap<LockType, Class<?>> SERVICE = new EnumMap<>(LockType.class);

    static {
        SERVICE.put(LockType.REENTRANT, ReentrantLockServiceImpl.class);
        SERVICE.put(LockType.FAIR, FairLockServiceImpl.class);
        SERVICE.put(LockType.READ, ReadLockServiceImpl.class);
        SERVICE.put(LockType.WRITE, WriteLockServiceImpl.class);
        SERVICE.put(LockType.RED, RedLockServiceImpl.class);
        SERVICE.put(LockType.MULTI, MultiLockServiceImpl.class);
    }

    /**
     * 根据类型进行不同的锁实现
     *
     * @param lockType 锁类
     * @return LockService
     */
    public LockService getLock(LockType lockType) {
        return (LockService) ApplicationContextHelper.getContext().getBean(SERVICE.get(lockType));
    }
}
