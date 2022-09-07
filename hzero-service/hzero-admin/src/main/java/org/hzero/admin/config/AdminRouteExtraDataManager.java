package org.hzero.admin.config;


import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import org.hzero.common.HZeroService;

/**
 * 服务路由配置
 */
@ChoerodonExtraData
public class AdminRouteExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Admin.CODE);
        choerodonRouteData.setPath(HZeroService.Admin.PATH);
        choerodonRouteData.setServiceId(HZeroService.Admin.NAME);
        choerodonRouteData.setStripPrefix(false);
        choerodonRouteData.setPackages("org.hzero.admin.api");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
