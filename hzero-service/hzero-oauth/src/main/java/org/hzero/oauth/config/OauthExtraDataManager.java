package org.hzero.oauth.config;


import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

import org.hzero.common.HZeroService;

@ChoerodonExtraData
public class OauthExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Oauth.CODE);
        choerodonRouteData.setPath(HZeroService.Oauth.PATH);
        choerodonRouteData.setServiceId(HZeroService.Oauth.NAME);
        choerodonRouteData.setPackages("org.hzero.oauth");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
