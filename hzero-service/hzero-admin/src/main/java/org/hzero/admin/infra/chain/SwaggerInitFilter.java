package org.hzero.admin.infra.chain;

import org.hzero.admin.domain.vo.InitChainContext;
import org.hzero.admin.domain.vo.Service;
import org.hzero.admin.infra.feign.SwaggerRefreshService;
import org.hzero.core.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * swagger文档初始化filter
 *
 * @author XCXCXCXCX
 * @date 2020/6/28 10:41 上午
 */
@Component
public class SwaggerInitFilter implements InitFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerInitFilter.class);

    @Autowired
    private SwaggerRefreshService swaggerRefreshService;

    @Override
    public void doFilter(InitChain chain, InitChainContext context) {
        Service service = context.getService();
        try {
            ResponseEntity<String> response = swaggerRefreshService.innerRefresh(service.getServiceName(), service.getVersion());
            if (ResponseUtils.isFailed(response)) {
                LOGGER.error("swagger refresh failed, try to refresh swagger manually! cause: {}", response.getBody() == null ? response.getStatusCodeValue() : response.getBody());
            }
        } catch (Throwable e) {
            LOGGER.error("swagger refresh failed, try to refresh swagger manually! ", e);
        } finally {
            chain.initNext(chain, context);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
