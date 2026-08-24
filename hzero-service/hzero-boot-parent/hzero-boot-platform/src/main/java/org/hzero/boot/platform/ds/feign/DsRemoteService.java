package org.hzero.boot.platform.ds.feign;

import org.hzero.boot.platform.ds.feign.fallback.DsRemoteServiceImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/08 9:51
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = DsRemoteServiceImpl.class)
public interface DsRemoteService {

    /**
     * 查询数据源
     *
     * @param organizationId 租户Id
     * @param datasourceCode 数据源编码
     * @param dsPurposeCode  数据源用途
     * @return 数据源
     */
    @GetMapping("/v1/{organizationId}/datasources/{datasourceCode}/detail")
    ResponseEntity<String> getByUnique(@PathVariable("organizationId") Long organizationId,
                                       @PathVariable("datasourceCode") String datasourceCode,
                                       @RequestParam("dsPurposeCode") String dsPurposeCode);
}
