package org.hzero.message.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.message.infra.feign.fallback.LangServiceFallBackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 语言
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/15 11:15
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = LangServiceFallBackImpl.class, path = "/v1")
public interface LangService {

    /**
     * 获取语言列表
     *
     * @return 语言
     */
    @GetMapping(value = "/languages/list")
    ResponseEntity<String> listLanguage();
}
