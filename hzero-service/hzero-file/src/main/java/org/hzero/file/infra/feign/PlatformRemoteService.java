package org.hzero.file.infra.feign;

import java.util.List;

import org.hzero.common.HZeroService;
import org.hzero.file.infra.feign.fallback.PlatformRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 平台服务远程调用
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/05 13:38
 */
@FeignClient(value = HZeroService.Platform.NAME, path = "/v1", fallback = PlatformRemoteServiceImpl.class)
public interface PlatformRemoteService {

    /**
     * 根据服务器Id查询服务器列表
     *
     * @param organizationId 租户ID
     * @param serverIdList   服务器ID
     * @return 列表
     */
    @GetMapping("/{organizationId}/servers/server-ids")
    ResponseEntity<String> listByServerIds(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("serverIdList") List<Long> serverIdList);

    /**
     * 根据集群Id查询服务器列表
     *
     * @param organizationId 租户ID
     * @param clustersIdList 集群ID
     * @return 列表
     */
    @GetMapping("/{organizationId}/servers/cluster-ids")
    ResponseEntity<String> listByClusterIds(@PathVariable("organizationId") Long organizationId,
                                            @RequestParam("clustersIdList") List<Long> clustersIdList);
}
