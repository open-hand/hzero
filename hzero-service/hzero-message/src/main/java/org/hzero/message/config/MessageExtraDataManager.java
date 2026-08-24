package org.hzero.message.config;

import org.hzero.common.HZeroService;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

/**
 * @author shuangfei.zhu@hand-china.com 2018-12-27
 */
@ChoerodonExtraData
public class MessageExtraDataManager implements ExtraDataManager {

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(environment.getProperty("hzero.service.current.name", HZeroService.Message.CODE));
        choerodonRouteData.setPath(environment.getProperty("hzero.service.current.path", HZeroService.Message.PATH));
        choerodonRouteData.setServiceId(environment.getProperty("hzero.service.current.service-name", HZeroService.Message.NAME));
        choerodonRouteData.setPackages("org.hzero.message");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
