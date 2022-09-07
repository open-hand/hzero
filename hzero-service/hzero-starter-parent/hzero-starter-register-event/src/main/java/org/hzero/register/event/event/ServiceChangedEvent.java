package org.hzero.register.event.event;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;


/**
 * 服务改变事件
 * @author XCXCXCXCX
 */
public class ServiceChangedEvent extends ApplicationEvent{

    private String serviceName;

    private ServiceInstance serviceInstance;

    public ServiceChangedEvent(Object source, String serviceName, ServiceInstance serviceInstance) {
        super(source);
        this.serviceName = serviceName;
        this.serviceInstance = serviceInstance;
    }

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
