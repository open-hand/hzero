package org.hzero.boot.admin.transport;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 11:09 上午
 */
public interface AddressService {

    List<ServiceInstance> getInstances();

}
