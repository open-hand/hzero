package org.hzero.scheduler.config;


import org.hzero.common.HZeroService;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

/**
 * @author shuangfei.zhu@hand-china.com
 */
@ChoerodonExtraData
public class SchedulerExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Scheduler.CODE);
        choerodonRouteData.setPath(HZeroService.Scheduler.PATH);
        choerodonRouteData.setServiceId(HZeroService.Scheduler.NAME);
        choerodonRouteData.setPackages("org.hzero.scheduler.api");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
