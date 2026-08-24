package org.hzero.file.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import org.hzero.common.HZeroService;

@ChoerodonExtraData
public class FileSystemExtraDataManager implements ExtraDataManager {

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(HZeroService.File.CODE);
        choerodonRouteData.setPath(HZeroService.File.PATH);
        choerodonRouteData.setServiceId(HZeroService.File.NAME);
        choerodonRouteData.setPackages("org.hzero.file.api");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
