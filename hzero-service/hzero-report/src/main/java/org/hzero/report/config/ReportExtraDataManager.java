package org.hzero.report.config;


import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

import org.hzero.common.HZeroService;

/**
 * 服务基本信息
 *
 * @author hzero
 */
@ChoerodonExtraData
public class ReportExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Report.CODE);
        choerodonRouteData.setPath(HZeroService.Report.PATH);
        choerodonRouteData.setServiceId(HZeroService.Report.NAME);
        choerodonRouteData.setPackages("org.hzero.report");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
