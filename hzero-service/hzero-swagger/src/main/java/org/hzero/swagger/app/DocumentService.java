package org.hzero.swagger.app;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.client.ServiceInstance;

import java.io.IOException;

public interface DocumentService {

    /**
     * 根据服务id和版本获取swagger json node
     * 加上security等json node
     *
     * @param service 服务名，形如hap-user-service
     * @param version 版本，可为空，为空时从默认版本中获取swagger json
     * @return swagger json node
     * @throws IOException json解析异常
     */
    ObjectNode getSwaggerJsonByIdAndVersion(String service, String version) throws IOException;

    /**
     * 根据服务id和版本获取swagger json
     * swagger表里有，则从swagger表里获取，没有则直接feign调用获取
     *
     * @param service 服务名，形如hap-user-service
     * @param version 版本，可为空，为空时从默认版本中获取swagger json
     * @return swagger json
     */
    String fetchSwaggerJsonByService(String service, String version);

    String getSwaggerJson(String name, String version) throws IOException;

    void manualRefresh(String serviceName, String version);

    void autoRefresh(String serviceName, String version);

    String fetchSwaggerJsonByIp(ServiceInstance serviceInstance);
}
