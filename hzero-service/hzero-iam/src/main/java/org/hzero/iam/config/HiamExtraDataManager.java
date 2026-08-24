package org.hzero.iam.config;


import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

import org.hzero.common.HZeroService;

@ChoerodonExtraData
public class HiamExtraDataManager implements ExtraDataManager {

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(environment.getProperty("hzero.service.current.name", HZeroService.Iam.CODE));
        choerodonRouteData.setPath(environment.getProperty("hzero.service.current.path", HZeroService.Iam.PATH));
        choerodonRouteData.setServiceId(environment.getProperty("hzero.service.current.service-name", HZeroService.Iam.NAME));
        choerodonRouteData.setPackages("org.hzero.iam");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
