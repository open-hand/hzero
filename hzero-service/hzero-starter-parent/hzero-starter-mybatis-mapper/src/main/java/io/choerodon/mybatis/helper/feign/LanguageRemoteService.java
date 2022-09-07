package io.choerodon.mybatis.helper.feign;

import io.choerodon.mybatis.helper.feign.failback.LanguageRemoteServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author qingsheng.chen@hand-china.com
 */
@FeignClient(value = "${hzero.service.platform.name:hzero-platform}", path = "v1/", fallback = LanguageRemoteServiceImpl.class)
public interface LanguageRemoteService {

    /**
     * 获取所有的语言
     *
     * @return 语言列表
     */
    @GetMapping("languages/list")
    ResponseEntity<String> listLanguage();
}
