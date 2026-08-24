package org.hzero.swagger.domain.repository;

import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.swagger.domain.entity.ServiceRoute;

/**
 * 服务路由资源库
 *
 * @author bojiangzhou 2018/12/13
 */
public interface ServiceRouteRepository extends BaseRepository<ServiceRoute> {


    /**
     * 查询所有服务路由，将所有服务路由分组排序，无关产品、环境
     */
    List<ServiceRoute> getAllRoute();

    /**
     * 获取运行中的服务实例路由 Map key={serviceId, metaVersion}
     */
    MultiKeyMap getAllRunningInstances();

}
