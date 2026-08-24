package org.hzero.admin.infra.chain;

import io.choerodon.core.exception.CommonException;
import org.hzero.admin.domain.vo.InitChainContext;
import org.hzero.admin.domain.vo.Service;
import org.hzero.admin.infra.feign.PermissionRefreshService;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 权限初始化filter
 *
 * @author XCXCXCXCX
 * @date 2020/6/28 10:40 上午
 */
@Component
public class PermissionInitFilter implements InitFilter, Ordered {

    @Autowired
    private PermissionRefreshService permissionRefreshService;

    @Override
    public void doFilter(InitChain chain, InitChainContext context) {
        Service service = context.getService();
        ResponseEntity<String> response = permissionRefreshService.innerRefresh(service.getServiceName(), service.getVersion());
        if (ResponseUtils.isFailed(response)) {
            throw new CommonException("permission refresh failed, cause: " + (response.getBody() == null ? response.getStatusCodeValue() : response.getBody()));
        }
        chain.initNext(chain, context);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
