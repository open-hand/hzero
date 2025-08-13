package org.hzero.config.client.api.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.hzero.config.client.api.ConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosConfigProperties;

/**
 * nacos 配置中心客户端的通用封装
 * @author XCXCXCXCX
 * @date 2019/9/23
 */
public class NacosConfigClient implements ConfigClient {

    @Autowired
    private NacosConfigProperties nacosConfigProperties;

    @Override
    public void publishConfig(String env, String serviceName, String version, String fileType, String content) {
        if(env == null) {
            env = "default";
        }
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        try {
            configService.publishConfig(serviceName + "-" + env +"." + fileType, nacosConfigProperties.getGroup(), content);
        } catch (NacosException e) {
            throw new RuntimeException("publish config failed", e);
        }
    }

    @Override
    public void publishConfigKeyValue(String env, String serviceName, String version, String key, String value) {
        if(env == null) {
            env = "default";
        }
        ConfigService configService = nacosConfigProperties.configServiceInstance();
        try {
            configService.publishConfig(serviceName + "-" + env +".properties", nacosConfigProperties.getGroup(), key + "=" + value);
        } catch (NacosException e) {
            throw new RuntimeException("publish config failed", e);
        }
    }
}
