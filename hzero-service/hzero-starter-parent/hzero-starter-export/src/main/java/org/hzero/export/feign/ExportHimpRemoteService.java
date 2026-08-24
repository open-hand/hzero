package org.hzero.export.feign;

import org.hzero.common.HZeroService;
import org.hzero.export.feign.fallback.ExportHimpRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 导入服务远程调用
 *
 * @author shuangfei.zhu@hand-china.com 2020/10/26 20:00
 */
@FeignClient(value = HZeroService.Import.NAME, fallback = ExportHimpRemoteServiceImpl.class)
public interface ExportHimpRemoteService {

    /**
     * 获取模板内容
     *
     * @param organizationId 租户Id
     * @param templateCode   模板编码
     * @param lang           语言
     * @return 模板
     */
    @GetMapping(value = "/v1/{organizationId}/template/{templateCode}/info/no-multi")
    ResponseEntity<String> getTemplate(@PathVariable(value = "organizationId") Long organizationId,
                                       @PathVariable(value = "templateCode") String templateCode,
                                       @RequestParam(value = "lang", required = false) String lang);
}
