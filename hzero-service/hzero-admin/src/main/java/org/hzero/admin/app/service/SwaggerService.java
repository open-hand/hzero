package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.swagger.ControllerDTO;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.Map;

/**
 * 应用服务
 *
 * @author bo.he02@hand-china.com 2020-05-09 11:00:41
 */
public interface SwaggerService {
    /**
     * 获取swagger资源
     *
     * @return swagger资源
     */
    List<SwaggerResource> getSwaggerResource();

    /**
     * 查询所有运行实例的api树形接口
     *
     * @return map
     */
    Map<String, List<Map<String, Object>>> queryTreeMenu();

    /**
     * 根据path的url和method查询单个path
     *
     * @param serviceName    服务名称
     * @param version        版本号
     * @param controllerName controller名称
     * @param operationId    操作ID
     * @return 查询结果
     */
    ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId);
}
