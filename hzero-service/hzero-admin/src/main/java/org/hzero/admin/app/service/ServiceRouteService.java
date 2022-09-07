package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceRoute;

import java.util.List;

/**
 * 服务路由配置应用服务
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
public interface ServiceRouteService {

    /**
     * 查询路由列表
     *
     * @param pageRequest  分页请求
     * @param serviceRoute 路由筛选条件
     * @return Page<ServiceRoute>
     */
    Page<ServiceRoute> pageServiceRoute(PageRequest pageRequest, ServiceRoute serviceRoute);

    /**
     * 查询列表
     * @param param 服务路由参数
     * @return List<ServiceRoute>
     */
    List<ServiceRoute> selectServiceRoute(ServiceRoute param);

    /**
     * 创建服务路由信息
     *
     * @param serviceRoute 路由信息
     * @return ServiceRoute
     */
    ServiceRoute createServiceRoute(ServiceRoute serviceRoute);

    /**
     * 更新路由信息
     *
     * @param serviceRoute 路由信息
     * @return ServiceRoute
     */
    ServiceRoute updateServiceRoute(ServiceRoute serviceRoute);

    /**
     * 删除服务路由
     * @param serviceRoute 服务路由实体
     */
    void deleteServiceRoute(ServiceRoute serviceRoute);

    ServiceRoute selectRouteDetails(Long serviceRouteId);
    List<ServiceRoute> selectRouteDetails(String serviceCode);
}
