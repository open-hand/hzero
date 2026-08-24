package org.hzero.admin.app.service.impl;

import org.hzero.admin.api.dto.swagger.ControllerDTO;
import org.hzero.admin.app.service.SwaggerService;
import org.hzero.admin.domain.repository.SwaggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.Map;

/**
 * 应用服务默认实现
 *
 * @author bo.he02@hand-china.com 2020-05-09 11:00:41
 */
@Service
public class SwaggerServiceImpl implements SwaggerService {
    /**
     * swagger资源库对象
     */
    private final SwaggerRepository swaggerRepository;

    @Autowired
    public SwaggerServiceImpl(SwaggerRepository swaggerRepository) {
        this.swaggerRepository = swaggerRepository;
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        return this.swaggerRepository.getSwaggerResource();
    }

    @Override
    public Map<String, List<Map<String, Object>>> queryTreeMenu() {
        return this.swaggerRepository.queryTreeMenu();
    }

    @Override
    public ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId) {
        return this.swaggerRepository.queryPathDetail(serviceName, version, controllerName, operationId);
    }
}
