package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 服务配置资源库
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
public interface ServiceConfigRepository extends BaseRepository<ServiceConfig> {

    /**
     * 查询默认产品环境下的配置
     *
     * @param serviceConfigId ID
     * @return ServiceConfig
     */
    ServiceConfig selectSelf(Long serviceConfigId);

    /**
     * 分页查询服务配置列表
     *
     * @param serviceConfig 服务配置实体
     * @param pageRequest   分页参数
     * @return Page<ServiceConfig>
     */
    Page<ServiceConfig> pageServiceConfigList(ServiceConfig serviceConfig, PageRequest pageRequest);

    /**
     * 根据版本查询服务配置，版本为空时查询默认
     *
     * @param serviceCode 服务编码
     * @param version     版本
     * @return ServiceConfig
     */
    ServiceConfig selectConfigWithVersion(String serviceCode, String version);

    /**
     * 查询配置数目
     * @param serviceCode
     * @param serviceVersion
     * @return 符合条件的配置数目
     */
    int selectConfigCount(String serviceCode, String serviceVersion);

}
