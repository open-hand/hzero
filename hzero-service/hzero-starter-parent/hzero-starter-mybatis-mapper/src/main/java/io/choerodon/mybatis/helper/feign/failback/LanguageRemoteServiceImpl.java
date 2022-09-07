package io.choerodon.mybatis.helper.feign.failback;

import io.choerodon.mybatis.helper.feign.LanguageRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class LanguageRemoteServiceImpl implements LanguageRemoteService {
    private static final Logger logger = LoggerFactory.getLogger(LanguageRemoteServiceImpl.class);
    @Override
    public ResponseEntity<String> listLanguage() {
        logger.error("Error get languages from platform.");
        return ResponseEntity.noContent().build();
    }
}
