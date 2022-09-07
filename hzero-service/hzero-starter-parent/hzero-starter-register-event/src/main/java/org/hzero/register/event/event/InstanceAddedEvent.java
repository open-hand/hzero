package org.hzero.register.event.event;


import org.springframework.cloud.client.ServiceInstance;

/**
 * 实例新增事件
 * @author XCXCXCXCX
 */
public class InstanceAddedEvent extends ServiceChangedEvent{
    public InstanceAddedEvent(Object source, String serviceName, ServiceInstance serviceInstance) {
        super(source, serviceName, serviceInstance);
    }
}
