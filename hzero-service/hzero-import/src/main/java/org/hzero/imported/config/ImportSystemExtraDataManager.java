package org.hzero.imported.config;

import io.choerodon.swagger.swagger.extra.ExtraData;
import org.hzero.common.HZeroService;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

/**
 * @author shuangfei.zhu@hand-china.com 2018-11-27
 */
@ChoerodonExtraData
public class ImportSystemExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.Import.CODE);
        choerodonRouteData.setPath(HZeroService.Import.PATH);
        choerodonRouteData.setServiceId(HZeroService.Import.NAME);
        choerodonRouteData.setPackages("org.hzero.imported.api");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }

}
