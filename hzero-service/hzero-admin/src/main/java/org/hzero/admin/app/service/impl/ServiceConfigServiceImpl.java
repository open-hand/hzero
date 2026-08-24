package org.hzero.admin.app.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.ServiceConfigService;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.admin.domain.repository.ServiceConfigRepository;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.domain.service.ConfigRefreshService;
import org.hzero.admin.domain.vo.ConfigParam;
import org.hzero.core.base.BaseAppService;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务配置应用服务默认实现
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
@Service
public class ServiceConfigServiceImpl extends BaseAppService implements ServiceConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfigServiceImpl.class);

    @Lazy
    @Autowired
    private ServiceConfigRepository configRepository;
    @Lazy
    @Autowired
    private ServiceRepository serviceRepository;
    @Lazy
    @Autowired
    private ConfigRefreshService configRefreshService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceConfig createConfig(ServiceConfig config) {
        validObject(config);
        // 校验
        config.validateAndInit(serviceRepository, configRepository);
        // yaml -> json
        config.yamlToJson();
        // 新增
        configRepository.insertSelective(config);

        // 通知刷新配置
        try {
            configRefreshService.notifyServiceRefresh(ConfigParam.build(config.getServiceCode(), config.getConfigVersion()));
        } catch (Exception e) {
            LOGGER.error("Notify service[serviceCode={}] refresh config error when create config.",
                    config.getServiceCode(), e);
        }

        return config;
    }

    @Override
    public ServiceConfig updateConfig(ServiceConfig config) {
        validObject(config);
        // 校验
        config.validateAndInit(serviceRepository, configRepository);
        // yaml -> json
        config.yamlToJson();
        // 更新
        configRepository.updateOptional(config,
                ServiceConfig.FIELD_CONFIG_VALUE,
                ServiceConfig.FIELD_CONFIG_YAML);

        // 通知刷新配置
        try {
            configRefreshService.notifyServiceRefresh(ConfigParam.build(config.getServiceCode(), config.getConfigVersion()));
        } catch (Exception e) {
            LOGGER.error("Notify service[serviceCode={}] refresh config error when update config.",
                    config.getServiceCode(), e);
        }

        return config;
    }

    @Override
    public void deleteServiceConfig(ServiceConfig config) {
        SecurityTokenHelper.validToken(config);

        ServiceConfig self = configRepository.selectSelf(config.getServiceConfigId());

        configRepository.deleteByPrimaryKey(self.getServiceConfigId());
    }

    @Override
    public ServiceConfig selectSelf(Long serviceConfigId) {
        return configRepository.selectSelf(serviceConfigId);
    }

    @Override
    public Page<ServiceConfig> pageServiceConfigList(ServiceConfig serviceConfig, PageRequest pageRequest) {
        return configRepository.pageServiceConfigList(serviceConfig, pageRequest);
    }

}
