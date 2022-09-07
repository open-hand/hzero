package org.hzero.boot.message.feign;

import org.hzero.boot.message.feign.fallback.PlatformRemoteImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/14 10:22
 */
@FeignClient(value = HZeroService.Platform.NAME, path = "/v1", fallback = PlatformRemoteImpl.class)
public interface PlatformRemoteService {

    /**
     * 获取在线用户
     *
     * @param organizationId 租户Id
     * @return 用户信息
     */
    @GetMapping("/{organizationId}/online-users/list")
    ResponseEntity<String> listOnlineUser(@PathVariable("organizationId") Long organizationId);
}
