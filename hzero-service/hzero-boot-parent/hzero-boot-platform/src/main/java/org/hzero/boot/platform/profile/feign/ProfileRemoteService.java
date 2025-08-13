package org.hzero.boot.platform.profile.feign;

import org.hzero.boot.platform.profile.feign.impl.ProfileRemoteServiceImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 配置维护Feign Service
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/08 19:32
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = ProfileRemoteServiceImpl.class, path = "/v1")
public interface ProfileRemoteService {

    /**
     * 按照层级依次查询配置维护值信息
     *
     * @param tenantId    租户Id
     * @param profileName 配置维护名称
     * @param userId      用户Id
     * @param roleId      角色Id
     * @return 当前访问最低层级的配置维护值
     */
    @GetMapping("/{organizationId}/profiles/value")
    ResponseEntity<String> getProfileValueByLevel(@PathVariable("organizationId") Long tenantId,
                    @RequestParam("profileName") String profileName, @RequestParam(required = false, name = "userId") Long userId,
                    @RequestParam(required = false, name = "roleId") Long roleId);
}
