package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.HServiceService;
import org.hzero.admin.app.service.ServiceRouteService;
import org.hzero.admin.domain.entity.HService;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.util.RouteValidateUtil;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务路由配置应用服务默认实现
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class ServiceRouteServiceImpl extends BaseAppService implements ServiceRouteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRouteServiceImpl.class);

    @Lazy
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;
    @Lazy
    @Autowired
    private HServiceService serviceService;
    @Lazy
    @Autowired
    private ServiceRepository serviceRepository;
    @Lazy
    @Autowired
    private ConfigRefreshService configRefreshService;

    @Override
    public Page<ServiceRoute> pageServiceRoute(PageRequest pageRequest, ServiceRoute param) {
        return PageHelper.doPage(pageRequest.getPage(), pageRequest.getSize(), () -> selectServiceRoute(param));
    }

    @Override
    public List<ServiceRoute> selectServiceRoute(ServiceRoute param) {
        return serviceRouteRepository.selectDefaultRoutes(param);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceRoute createServiceRoute(ServiceRoute route) {
        validObject(route);
        RouteValidateUtil.assertDangerousRoute(route.getPath());

        //校验是否存在服务
        List<HService> services = serviceRepository.select(new HService().setServiceCode(route.getServiceCode()));
        HService service;
        if (services.isEmpty()){
            // 插入服务
            service = serviceService.createService(route.buildService());
        } else if (services.size() == 1){
            service = services.get(0);
            LOGGER.debug("service[serviceCode=" + route.getServiceCode() + "] already exists, skip create service.");
        } else {
            //一般情况下不会出现，当数据库表结构及数据存在问题时出现。
            LOGGER.error("service[serviceCode=" + route.getServiceCode() + "] exists more than 1, check the data in the database please.");
            throw new CommonException("hadm.error.data_error");
        }

        // 设置服务ID
        route.setServiceId(service.getServiceId());
        // 检查路由是否存在
        route.checkExists(serviceRouteRepository);

        // insert
        serviceRouteRepository.insertSelective(route);

        // 刷新路由
        try {
            configRefreshService.notifyGatewayRefresh();
        } catch (Exception e) {
            LOGGER.warn("Notice fresh service route error when create route.", e);
        }
        return route;
    }

    @Override
    public ServiceRoute updateServiceRoute(ServiceRoute serviceRoute) {
        validObject(serviceRoute);
        SecurityTokenHelper.validToken(serviceRoute);
        RouteValidateUtil.assertDangerousRoute(serviceRoute.getPath());

        serviceRouteRepository.updateOptional(serviceRoute,
                ServiceRoute.FIELD_SENSITIVE_HEADERS,
                ServiceRoute.FIELD_STRIP_PREFIX,
                ServiceRoute.FIELD_URL,
                ServiceRoute.FIELD_NAME);

        // 刷新路由
        try {
            configRefreshService.notifyGatewayRefresh();
        } catch (Exception e) {
            LOGGER.warn("Notice fresh service route error when update route. ", e);
        }
        return serviceRoute;
    }

    @Override
    public void deleteServiceRoute(ServiceRoute serviceRoute) {

        SecurityTokenHelper.validToken(serviceRoute);

        serviceRouteRepository.deleteByPrimaryKey(serviceRoute.getServiceRouteId());
        // 刷新路由
        try {
            configRefreshService.notifyGatewayRefresh();
        } catch (Exception e) {
            LOGGER.warn("Notice fresh service route error when delete route. ", e);
        }
    }

    @Override
    public ServiceRoute selectRouteDetails(Long serviceRouteId) {
        return serviceRouteRepository.selectRouteDetails(serviceRouteId);
    }

    @Override
    public List<ServiceRoute> selectRouteDetails(String serviceCode) {
        return serviceRouteRepository.selectRouteDetails(serviceCode);
    }

}
