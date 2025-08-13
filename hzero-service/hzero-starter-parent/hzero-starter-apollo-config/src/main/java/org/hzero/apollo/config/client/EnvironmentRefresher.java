package org.hzero.apollo.config.client;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.hzero.apollo.config.client.namespace.NamespaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.refresh.ContextRefresher;

/**
 * 环境变量刷新类，用于刷新Spring上下文的配置项
 * @author XCXCXCXCX
 */
public class EnvironmentRefresher implements ConfigChangeListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentRefresher.class);

    private final ContextRefresher contextRefresher;

    private final ApolloConfigListenerProperties properties;

    public EnvironmentRefresher(ContextRefresher contextRefresher, ApolloConfigListenerProperties properties) {
        this.properties = properties;
        this.contextRefresher = contextRefresher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for(String namespace : NamespaceManager.get()){
            Config config = ConfigService.getConfig(namespace);
            if(properties.getInterestedKeys() == null && properties.getInterestedKeyPrefixes() == null){
                config.addChangeListener(this);
            }else{
                config.addChangeListener(this, properties.getInterestedKeys(), properties.getInterestedKeyPrefixes());
            }
        }
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        refreshEnvironment(changeEvent);
    }

    private void refreshEnvironment(ConfigChangeEvent changeEvent) {
        LOGGER.info("Refreshing environment! keys changed:{}", changeEvent.changedKeys().toString());
        contextRefresher.refresh();
        LOGGER.info("Refreshed environment!");
    }

}
