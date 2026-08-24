package org.hzero.platform.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.platform.api.dto.RoleDTO;
import org.hzero.platform.infra.feign.impl.HiamRemoteServiceFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * HZERO-IAM 远程服务
 * </p>
 *
 * @author qingsheng.chen 2018/7/13 星期五 10:01
 */
@FeignClient(value = HZeroService.Iam.NAME, fallback = HiamRemoteServiceFallbackImpl.class, path = "hzero/v1/")
public interface HiamRemoteService {

    /**
     * 查询用户角色信息.
     *
     * @param tenantId
     * @param roleId
     * @return 用户角色
     */
    @GetMapping("{organizationId}/roles/{roleId}")
    RoleDTO getRole(@PathVariable("organizationId") Long tenantId, @RequestParam("roleId") Long roleId);

}
