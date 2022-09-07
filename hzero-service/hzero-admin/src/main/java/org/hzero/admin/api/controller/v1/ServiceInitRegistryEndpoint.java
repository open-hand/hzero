package org.hzero.admin.api.controller.v1;

import org.hzero.admin.api.dto.ServiceRegistryResponse;
import org.hzero.admin.app.service.ServiceInitRegistry;
import org.hzero.admin.domain.vo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:01 上午
 */
@RestControllerEndpoint(id = "service-init-registry")
public class ServiceInitRegistryEndpoint {

    @Autowired
    private ServiceInitRegistry serviceInitRegistry;

    @PostMapping("/register")
    public ServiceRegistryResponse<?> register(@RequestBody Service dto) {
        return serviceInitRegistry.register(dto);
    }

    @PostMapping("/unregister")
    public ServiceRegistryResponse<?> unregister(@RequestBody Service dto) {
        return serviceInitRegistry.unregister(dto);
    }

    @GetMapping
    public Map<String, Set<Service>> services() {
        Map<String, Set<Service>>  setMap = new HashMap<>();
        serviceInitRegistry.getServices()
                .forEach(service -> {
                    Set<Service> services = setMap.computeIfAbsent(service.getServiceName(), k -> new HashSet<>());
                    services.add(service);
                });
        return setMap;
    }

}
