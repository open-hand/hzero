package org.hzero.admin.app.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:18 下午
 */
public interface ServiceRouteRefreshService {
    void notifyGateway();

    void refreshRouteAndNotifyGateway(String serviceName, String version);

    void initRouteExtendConfigAndNotifyGateway(List<Long> routeIds);

    @Transactional(rollbackFor = RuntimeException.class)
    void removeRouteExtendConfigAndNotifyGateway(String fieldName, List<Long> routeIds);
}
