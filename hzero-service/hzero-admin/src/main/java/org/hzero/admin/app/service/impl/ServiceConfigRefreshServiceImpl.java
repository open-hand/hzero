package org.hzero.admin.app.service.impl;

import org.hzero.admin.app.service.ServiceConfigRefreshService;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.admin.domain.vo.ConfigParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:25 下午
 */
@Service
public class ServiceConfigRefreshServiceImpl implements ServiceConfigRefreshService {

    @Autowired
    private ConfigRefreshService configRefreshService;

    @Override
    public void refreshApplicationContext(String serviceName, String version) {
        configRefreshService.notifyServiceRefresh(ConfigParam.build(serviceName, version));
    }

}
