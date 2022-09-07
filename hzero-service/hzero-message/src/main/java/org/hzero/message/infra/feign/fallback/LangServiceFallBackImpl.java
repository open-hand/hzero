package org.hzero.message.infra.feign.fallback;

import org.hzero.core.util.Results;
import org.hzero.message.infra.feign.LangService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 语言
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/15 11:15
 */
@Component
public class LangServiceFallBackImpl implements LangService {

    @Override
    public ResponseEntity<String> listLanguage() {
        return Results.success();
    }
}