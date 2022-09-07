package org.hzero.boot.admin.transport;

import org.hzero.boot.admin.exception.TransportException;
import org.hzero.boot.admin.registration.Registration;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 8:59 上午
 */
public interface Transport {

    /**
     * 发起请求的通讯方法
     * 会抛异常，如果无法通讯成功，则服务反复报错，直到通讯成功
     * @throws TransportException
     */
    void transport(Registration registration) throws TransportException;

}
