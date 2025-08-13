package org.hzero.admin.infra.feign;

import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author XCXCXCXCX
 * @date 2019/9/6
 * @project hzero-admin
 */
@FeignClient(
        value = HZeroService.Iam.NAME,
        configuration= FeignLogger.class
)
public interface PermissionRefreshService {

    /**
     * 用于手动刷新
     * @param serviceName
     * @param metaVersion
     * @return
     */
    @PostMapping("/v1/tool/permission/fresh")
    ResponseEntity refresh(@RequestParam("serviceName") String serviceName,
                           @RequestParam(value = "metaVersion", required = false) String metaVersion);

    /**
     * 用于自动刷新
     * @param serviceName
     * @param metaVersion
     * @return
     */
    @PostMapping("/v1/tool/permission/inner/fresh")
    ResponseEntity<String> innerRefresh(@RequestParam("serviceName") String serviceName,
                                        @RequestParam(value = "metaVersion", required = false) String metaVersion);

    @GetMapping("/hzero/v1/{organizationId}/roles/{roleId}")
    ResponseEntity<String> getRole(@PathVariable("organizationId") Long tenantId, @RequestParam("roleId") Long roleId);
}
