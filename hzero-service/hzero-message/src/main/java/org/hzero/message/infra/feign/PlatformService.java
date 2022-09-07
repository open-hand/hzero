package org.hzero.message.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.message.infra.feign.fallback.PlatformRemoteServiceFallBackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * platform调用
 *
 * @author fanghan.liu 2020/05/14 17:27
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = PlatformRemoteServiceFallBackImpl.class)
public interface PlatformService {

    /**
     * 根据用户id获取三方平台用户id
     *
     * @param organizationId    租户ID
     * @param ids               用户id
     * @param thirdPlatformType 三方平台类型
     * @return id
     */
    @GetMapping("/v1/{organizationId}/employee-users/open-userid")
    ResponseEntity<String> getOpenUserIdsByUserIds(@PathVariable("organizationId") Long organizationId,
                                                   @RequestParam("ids") String ids,
                                                   @RequestParam("thirdPlatformType") String thirdPlatformType);
}
