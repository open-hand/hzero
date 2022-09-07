package org.hzero.boot.platform.lov.feign.fallback;

import java.util.Collections;
import java.util.List;

import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;

/**
 * Lov服务Feign客户端fall back工厂类
 *
 * @author gaokuo.dai@hand-china.com 2018年7月5日下午3:08:49
 */
@Component
public class LovFeignClientFallbackFactory implements FallbackFactory<LovFeignClient> {

    private Logger logger = LoggerFactory.getLogger(LovFeignClientFallbackFactory.class);

    @Override
    public LovFeignClient create(Throwable cause) {

        // do not log error info in create function
        // this function will be called by
        // org.springframework.cloud.netflix.feign.HystrixTargeter.targetWithFallbackFactory(String,
        // FeignContext, HardCodedTarget<T>, Builder, Class<?>)
        // when feign client created by annotation with FallbackFactory
        // to test the generic declared correctly

        return new LovFeignClient() {

            private static final String COMMON_ERROR_MSG = "can not get response from hzero-platform, cause by";

            @Override
            public String queryLovSql(String lovCode, Long tenantId) {
                this.logError();
                return null;
            }

            @Override
            public String queryTranslationSql(String lovCode, Long tenantId) {
                this.logError();
                return null;
            }

            @Override
            public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId) {
                this.logError();
                return Collections.emptyList();
            }

            @Override
            public List<LovValueDTO> queryLovValueWithLanguage(String lovCode, Long tenantId, String lang) {
                this.logError();
                return Collections.emptyList();
            }

            @Override
            public LovDTO queryLovInfo(String lovCode, Long tenantId, String lang) {
                this.logError();
                return null;
            }

            @Override
            public LovViewDTO queryLovViewInfo(String viewCode, Long tenantId, String lang) {
                this.logError();
                return null;
            }

            @Override
            public String queryLovSql(String lovCode, Long tenantId, String lang) {
                this.logError();
                return null;
            }

            @Override
            public String queryTranslationSql(String lovCode, Long tenantId, String lang) {
                this.logError();
                return null;
            }

            @Override
            public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String lang) {
                this.logError();
                return null;
            }

            @Override
            public LovDTO queryLovInfo(String lovCode, Long tenantId) {
                this.logError();
                return null;
            }

            @Override
            public LovViewDTO queryLovViewInfo(String viewCode, Long tenantId) {
                this.logError();
                return null;
            }

            /**
             * 打印错误信息
             */
            private void logError() {
                logger.error(COMMON_ERROR_MSG);
                logger.error(cause.getMessage(), cause);
            }
        };
    }

}
