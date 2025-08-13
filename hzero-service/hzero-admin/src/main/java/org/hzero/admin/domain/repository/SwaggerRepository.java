package org.hzero.admin.domain.repository;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.hzero.admin.api.dto.swagger.ControllerDTO;
import org.hzero.admin.domain.entity.Swagger;
import org.hzero.mybatis.base.BaseRepository;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 资源库
 *
 * @author bo.he02@hand-china.com 2020-05-09 11:00:41
 */
public interface SwaggerRepository extends BaseRepository<Swagger> {
    /**
     * 通过服务的名称和版本，获取服务的json数据
     *
     * @param service 服务的名称
     * @param version 服务的版本
     * @return 服务的json数据
     */
    String fetchSwaggerJsonByService(String service, String version);

    /**
     * 获取swagger资源
     *
     * @return swagger资源
     */
    List<SwaggerResource> getSwaggerResource();

    /**
     * 获取各个服务数据
     *
     * @return 各个服务的数据      MultiKeyMap      key --> value === (routeName, serviceId) ---> Set<version>
     */
    MultiKeyMap<String, Set<String>> getServiceMetaDataMap();

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
