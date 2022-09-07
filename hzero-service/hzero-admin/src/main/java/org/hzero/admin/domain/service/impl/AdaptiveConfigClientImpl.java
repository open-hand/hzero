package org.hzero.admin.domain.service.impl;

import org.hzero.admin.domain.service.ConfigClient;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.admin.domain.repository.ServiceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author XCXCXCXCX
 * @date 2019/9/23
 * @project hzero-admin
 */
@Component
public class AdaptiveConfigClientImpl implements ConfigClient {

    @Autowired
    private org.hzero.config.client.api.ConfigClient configClient;

    @Autowired
    private ServiceConfigRepository configRepository;

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public void publishConfig(String serviceName, String version) {
        ServiceConfig config = getServiceConfig(serviceName, version);
        configClient.publishConfig(env, serviceName, version,"yaml", config == null ? null : config.getConfigYaml());
    }

    private ServiceConfig getServiceConfig(String serviceName, String version) {
        ServiceConfig queryParam = new ServiceConfig();
        queryParam.setServiceCode(serviceName);
        queryParam.setConfigVersion(version);
        return configRepository.selectOne(queryParam);
    }
}
