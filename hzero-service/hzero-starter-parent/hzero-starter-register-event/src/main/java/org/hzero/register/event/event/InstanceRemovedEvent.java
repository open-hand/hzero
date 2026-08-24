package org.hzero.register.event.event;

import org.springframework.cloud.client.ServiceInstance;


/**
 * 实例移除事件
 * @author XCXCXCXCX
 */
public class InstanceRemovedEvent extends ServiceChangedEvent{

    public InstanceRemovedEvent(Object source, String serviceName, ServiceInstance serviceInstance) {
        super(source, serviceName, serviceInstance);
    }
}
