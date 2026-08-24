package org.hzero.admin.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.admin.domain.vo.ConfigParam;

/**
 * 服务配置应用服务
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
public interface ServiceConfigService {

    /**
     * 保存或更新服务配置
     *
     * @param serviceConfig 服务配置实体
     * @return ServiceConfig
     */
    ServiceConfig createConfig(ServiceConfig serviceConfig);

    /**
     * 修改配置
     *
     * @param serviceConfig 配置信息
     * @return ServiceConfig
     */
    ServiceConfig updateConfig(ServiceConfig serviceConfig);

    /**
     * 删除配置，默认配置不可删除
     *
     * @param serviceConfig 配置信息
     */
    void deleteServiceConfig(ServiceConfig serviceConfig);

    ServiceConfig selectSelf(Long serviceConfigId);

    Page<ServiceConfig> pageServiceConfigList(ServiceConfig serviceConfig, PageRequest pageRequest);
}
