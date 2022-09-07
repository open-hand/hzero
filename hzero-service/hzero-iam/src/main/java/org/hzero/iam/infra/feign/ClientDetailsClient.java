package org.hzero.iam.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.iam.infra.feign.fallback.ClientDetailsClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 客户端缓存信息修改
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:09
 */
@FeignClient(value = HZeroService.Oauth.NAME, fallback = ClientDetailsClientImpl.class, path = "/oauth/api/client")
public interface ClientDetailsClient {

    /**
     * 更新客户端的当前角色
     *
     * @param accessToken 当前客户端的token
     * @param roleId      当前角色
     * @param assignLevel 分配层级
     * @param assignValue 分配层级值
     * @return 状态码
     */
    @PostMapping("/role-id")
    ResponseEntity storeClientRole(@RequestParam("access_token") String accessToken,
                                   @RequestParam("roleId") Long roleId,
                                   @RequestParam("assignLevel") String assignLevel,
                                   @RequestParam("assignValue") Long assignValue);

    /**
     * 更新客户端的当前租户
     *
     * @param accessToken 当前客户端的token
     * @param tenantId    当前租户
     * @return 状态码
     */
    @PostMapping("/tenant-id")
    ResponseEntity storeClientTenant(@RequestParam("access_token") String accessToken, @RequestParam("tenantId") Long tenantId);

}
