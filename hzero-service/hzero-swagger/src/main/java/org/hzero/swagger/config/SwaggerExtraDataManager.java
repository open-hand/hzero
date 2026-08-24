package org.hzero.swagger.config;


import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

import org.hzero.common.HZeroService;

@ChoerodonExtraData
public class SwaggerExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Swagger.CODE);
        choerodonRouteData.setPath(HZeroService.Swagger.PATH);
        choerodonRouteData.setServiceId(HZeroService.Swagger.NAME);
        choerodonRouteData.setPackages("org.hzero.swagger");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
