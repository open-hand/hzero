package org.hzero.boot.imported.infra.feign;

import org.hzero.boot.imported.infra.feign.fallback.TemplateRemoteServiceImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * @author : chunqiang.bai@hand-china.com
 */
@FeignClient(value = HZeroService.Import.NAME, fallback = TemplateRemoteServiceImpl.class)
public interface TemplateRemoteService {

    /**
     * 获取模板内容
     *
     * @param organizationId 租户Id
     * @param templateCode   模板编码
     * @return 模板
     */
    @GetMapping(value = "/v1/{organizationId}/template/{templateCode}/info/no-multi")
    ResponseEntity<String> getTemplate(@PathVariable(value = "organizationId") Long organizationId, @PathVariable(value = "templateCode") String templateCode);
}
