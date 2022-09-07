package org.hzero.admin.domain.repository;


import org.apache.commons.collections4.map.MultiKeyMap;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 服务路由配置资源库
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:53
 */
public interface ServiceRouteRepository extends BaseRepository<ServiceRoute> {

    /**
     * 查询默认产品环境路由信息
     */
    List<ServiceRoute> selectDefaultRoutes(ServiceRoute serviceRoute);

    /**
     * 查询路由明细
     *
     * @param serviceRouteId 服务路由id
     * @return ServiceRoute
     */
    ServiceRoute selectRouteDetails(Long serviceRouteId);

    /**
     * 查询路由(一个服务可能存在多个路由)
     *
     * @param serviceCode 服务编码
     * @return ServiceRoute
     */
    List<ServiceRoute> selectRouteDetails(String serviceCode);

    /**
     * 获取所有正在运行实例ZuulRoute
     *
     * @return 正在运行实例ZuulRoute key ---> value === (serviceId, version) ---> List<ServiceRoute>
     */
    MultiKeyMap<String, List<ServiceRoute>> getAllRunningInstances();
}
