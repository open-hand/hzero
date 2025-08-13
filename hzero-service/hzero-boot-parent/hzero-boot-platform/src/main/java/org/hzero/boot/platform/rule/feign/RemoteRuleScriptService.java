package org.hzero.boot.platform.rule.feign;

import org.hzero.boot.platform.rule.feign.impl.RemoteRuleScriptServiceFallBackImpl;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * feign调用
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 17:48
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = RemoteRuleScriptServiceFallBackImpl.class)
public interface RemoteRuleScriptService {

    /**
     * 查询规则引擎配置
     *
     * @param scriptCode     规则编码
     * @param organizationId 租户Id
     * @return 规则引擎配置
     */
    @GetMapping("/v1/{organizationId}/rule-scripts/code")
    ResponseEntity<String> selectRuleScriptByCode(@RequestParam("scriptCode") String scriptCode,
                                                  @PathVariable("organizationId") Long organizationId);
}
