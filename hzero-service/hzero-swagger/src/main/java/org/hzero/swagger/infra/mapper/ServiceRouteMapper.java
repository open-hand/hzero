package org.hzero.swagger.infra.mapper;

import java.util.List;

import org.hzero.swagger.domain.entity.ServiceRoute;

import io.choerodon.mybatis.common.BaseMapper;

public interface ServiceRouteMapper extends BaseMapper<ServiceRoute> {

    List<ServiceRoute> selectRoutes(ServiceRoute param);
}
