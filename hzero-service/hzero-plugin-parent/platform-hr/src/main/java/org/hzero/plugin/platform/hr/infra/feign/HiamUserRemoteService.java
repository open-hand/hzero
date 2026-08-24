package org.hzero.plugin.platform.hr.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.plugin.platform.hr.infra.feign.impl.HiamUserRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * feign
 *
 * @author xiaoyu.zhao@hand-china.com 2020/03/25 9:25
 */
@FeignClient(value = HZeroService.Iam.NAME, fallbackFactory = HiamUserRemoteServiceImpl.class, path = "/hzero/v1")
public interface HiamUserRemoteService {

    /**
     * 锁定用户
     *
     * @param organizationId 租户ID
     * @param userId  用户ID
     */
    @RequestMapping(value = "/{organizationId}/users/{userId}/locked", method = RequestMethod.POST)
    ResponseEntity lockUser(@PathVariable Long organizationId, @PathVariable Long userId);
}
