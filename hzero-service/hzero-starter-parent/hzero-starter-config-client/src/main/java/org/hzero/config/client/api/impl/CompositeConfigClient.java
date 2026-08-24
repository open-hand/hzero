package org.hzero.config.client.api.impl;

import org.hzero.config.client.api.ConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 综合配置中心客户端
 *
 * @author XCXCXCXCX
 * @date 2019/9/26
 */
public class CompositeConfigClient implements ConfigClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompositeConfigClient.class);

    private final List<ConfigClient> clients;

    public CompositeConfigClient(List<ConfigClient> clients) {
        if(CollectionUtils.isEmpty(clients)){
            LOGGER.warn("no config client, config management does not take effect!");
            clients = new ArrayList<>();
            clients.add(new ConfigClient() {
                @Override
                public void publishConfig(String env, String serviceName, String version, String fileType, String content) {
                    throw new UnsupportedOperationException("unsupported config client");
                }
                @Override
                public void publishConfigKeyValue(String env, String serviceName, String version, String key, String value) {
                    throw new UnsupportedOperationException("unsupported config client");
                }
            });
        }
        this.clients = clients;
    }

    @Override
    public void publishConfig(String env, String serviceName, String version, String fileType, String content) {
        clients.forEach(client -> client.publishConfig(env, serviceName, version, fileType, content));
    }

    @Override
    public void publishConfigKeyValue(String env, String serviceName, String version, String key, String value) {
        clients.forEach(client -> client.publishConfigKeyValue(env, serviceName, version, key, value));
    }
}
