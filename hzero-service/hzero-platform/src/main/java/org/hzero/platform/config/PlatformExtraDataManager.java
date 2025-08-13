package org.hzero.platform.config;


import org.hzero.common.HZeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

/**
 * 服务基本信息
 *
 * @author gaokuo.dai@hand-china.com 2018年7月20日下午4:49:12
 */
@ChoerodonExtraData
public class PlatformExtraDataManager implements ExtraDataManager {

    @Autowired
    private Environment environment;
    @Override
    public ExtraData getData() {
        ChoerodonRouteData routeData = new ChoerodonRouteData();
        routeData.setName(environment.getProperty("hzero.service.current.name", HZeroService.Platform.CODE));
        routeData.setPath(environment.getProperty("hzero.service.current.path", HZeroService.Platform.PATH));
        routeData.setServiceId(environment.getProperty("hzero.service.current.service-name", HZeroService.Platform.NAME));
        routeData.setPackages("org.hzero.platform.api,org.hzero.plugin.platform");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, routeData);
        return extraData;
    }

}
