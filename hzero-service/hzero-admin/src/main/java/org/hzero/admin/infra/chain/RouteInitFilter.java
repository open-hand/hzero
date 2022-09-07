package org.hzero.admin.infra.chain;

import org.hzero.admin.domain.service.ParseRouteService;
import org.hzero.admin.domain.vo.InitChainContext;
import org.hzero.admin.domain.vo.Service;
import org.hzero.admin.infra.exception.ServiceSkipInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 路由初始化filter
 *
 * @author XCXCXCXCX
 * @date 2020/6/28 10:41 上午
 */
@Component
public class RouteInitFilter implements InitFilter, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteInitFilter.class);

    @Autowired
    private ParseRouteService routeRefreshService;

    @Override
    public void doFilter(InitChain chain, InitChainContext context) {
        Service service = context.getService();
        try {
            routeRefreshService.parser(service.getServiceName(), service.getVersion());
            chain.initNext(chain, context);
        } catch (ServiceSkipInitializationException e) {
            // 服务不存在路由信息，无需执行初始化操作（路由、权限、api）
            LOGGER.error(e.getCode());
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
