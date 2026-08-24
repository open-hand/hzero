package org.hzero.plugin.platform.hr.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.plugin.platform.hr.infra.feign.impl.HmsgUserReceiverConfigRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description
 *
 * @author fanghan.liu 2020/04/14 14:15
 */
@FeignClient(value = HZeroService.Message.NAME, fallback = HmsgUserReceiverConfigRemoteServiceImpl.class, path = "/v1")
public interface HmsgUserReceiverConfigRemoteService {

    /**
     * 刷新租户下用户消息接收配置
     * @param tenantId 租户ID
     *
     * @return void
     */
    @GetMapping("/v1/user-receive-configs/refresh/tenant")
    ResponseEntity<Void> refreshTenant(@RequestParam("tenantId") Long tenantId);
}
