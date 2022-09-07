package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.ServiceRoute;

import java.util.List;

/**
 * 服务路由配置Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:53
 */
public interface ServiceRouteMapper extends BaseMapper<ServiceRoute> {

    /**
     * 查询默认产品环境 productId=0 and productEnvId = 0
     */
    List<ServiceRoute> selectDefaultRoutes(ServiceRoute param);

    /**
     * 根据产品和环境查询路由信息
     */
    List<ServiceRoute> selectProductRoutes(ServiceRoute param);

    /**
     * 查询路由信息
     *
     * @param serviceRouteId 路由Id
     * @return ServiceRoute
     */
    ServiceRoute selectRouteDetails(@Param("serviceRouteId") Long serviceRouteId);
}
