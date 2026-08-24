package org.hzero.admin.api.controller.v1;

import io.choerodon.swagger.annotation.Permission;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 仅提供给网关拉取路由信息
 */
@RestController("routeFetchController.v1")
public class RouteFetchController {

    @Autowired
    private ServiceRouteRepository serviceRouteRepository;

    /**
     * 拉取所有路由（提供给网关）
     * @return
     */
    @Permission(permissionWithin = true)
    @GetMapping("/routes")
    public List<ServiceRoute> routes() {
        return serviceRouteRepository.selectDefaultRoutes(ServiceRoute.EMPTY);
    }
}
