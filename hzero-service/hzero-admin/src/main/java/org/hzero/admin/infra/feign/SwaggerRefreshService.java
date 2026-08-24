package org.hzero.admin.infra.feign;

import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author XCXCXCXCX
 * @date 2019/9/12
 * @project hzero-admin
 */
@FeignClient(
        value = HZeroService.Swagger.NAME,
        configuration= FeignLogger.class
)
public interface SwaggerRefreshService {

    /**
     * 用于手动刷新
     * @param serviceName
     * @param version
     * @return
     */
    @PostMapping(value = "/docs/swagger/refresh/{serviceName}")
    ResponseEntity refresh(@PathVariable("serviceName") String serviceName,
                           @RequestParam(value = "version", required = false) String version);

    /**
     * 用于自动刷新
     * @param serviceName
     * @param version
     * @return
     */
    @PostMapping(value = "/docs/inner/swagger/refresh/{serviceName}")
    ResponseEntity<String> innerRefresh(@PathVariable("serviceName") String serviceName,
                                                   @RequestParam(value = "version", required = false) String version);

}
