package org.hzero.config.app.service.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.config.app.service.ConfigListener;
import org.hzero.config.app.service.FailureCounter;
import org.hzero.config.app.service.ServiceConfigService;
import org.hzero.config.domain.entity.ServiceConfig;
import org.hzero.config.domain.repository.ServiceConfigRepository;
import org.hzero.config.domain.vo.Config;
import org.hzero.config.domain.vo.ConfigParam;
import org.hzero.config.infra.util.config.ConfigFileFormat;
import org.hzero.config.infra.util.config.ConfigFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务配置应用服务默认实现
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
@Service
public class ServiceConfigServiceImpl implements ServiceConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfigServiceImpl.class);

    private static final int LISTENER_MAX_FAILURE_TIMES = 100;

    private final Map<String, List<ConfigListener>> configListeners = new ConcurrentHashMap<>();

    @Autowired
    private ServiceConfigRepository configRepository;

    @Override
    public Config selectConfig(String serviceName, String label) {
        ConfigParam param = ConfigParam.build(serviceName, label);
        ServiceConfig serviceConfig = configRepository.selectDefaultConfig(param);

        Config config = new Config();
        config.setName(serviceName);
        config.setConfigVersion(label);

        if (serviceConfig != null) {
            config.setValue(serviceConfig.jsonToMap());
        }

        return config;
    }

    @Override
    public Map<String, Object> getConfig(String serviceName, String label) {
        Config config = selectConfig(serviceName, label);
        if (config == null) {
            return null;
        }
        return config.getValue();
    }

    @Override
    public void publishConfig(String serviceName, String label, String fileType, String content) {
        ServiceConfig param = new ServiceConfig();
        param.setServiceCode(serviceName);
        param.setConfigVersion(label);
        ServiceConfig serviceConfig = configRepository.selectOne(param);

        ConfigFileFormat configFileFormat = ConfigFileFormat.fromString(fileType);
        Map<String, Object> configValue = ConfigFormatUtils.convertTextToMap(configFileFormat, content);

        /**
         * 该服务及标签所对应的配置文件不存在时，创建新的配置文件
         */
        if (serviceConfig == null ) {
            ServiceConfig config = new ServiceConfig();
            config.setServiceCode(serviceName);
            config.setConfigVersion(label);
            config.setConfigValue(JSON.toJSONString(configValue));
            configRepository.insertOptional(config,
                    ServiceConfig.FIELD_SERVICE_CODE,
                    ServiceConfig.FIELD_CONFIG_VERSION,
                    ServiceConfig.FIELD_CONFIG_VALUE);
        } else {//否则把新的配置追加到原有的配置文件上
            @SuppressWarnings("unchecked")
            Map<String, Object> config = JSON.parseObject(serviceConfig.getConfigValue(), Map.class);
            /**
             * 旧配置会被覆盖，新配置会被追加
             */
            for (Map.Entry<String, Object> entry : configValue.entrySet()) {
                config.put(entry.getKey(), entry.getValue());
            }
            serviceConfig.setConfigValue(JSON.toJSONString(config));
            configRepository.updateByPrimaryKey(serviceConfig);
        }

        onChange(serviceName, label, serviceConfig == null ? null : convertProperties(serviceConfig.getConfigValue(), ConfigFileFormat.JSON), convertProperties(content, ConfigFileFormat.fromString(fileType)));
    }

    private Map<String, Object> convertProperties(String content, ConfigFileFormat format) {
        if (ConfigFileFormat.PROPERTIES == format) {
            return ConfigFormatUtils.convertTextToMap(ConfigFileFormat.PROPERTIES, content);
        } else if (ConfigFileFormat.JSON == format || ConfigFileFormat.YAML == format) {
            return ConfigFormatUtils.jsonToProperties(ConfigFormatUtils.convertTextToMap(format, content));
        }
        return new HashMap<>(2);
    }

    private void onChange(String serviceName, String label, Map<String, Object> old, String key, Object value) {
        Object oldValue = old == null ? null : old.get(key);
        if (!Objects.equals(oldValue, value)) {
            List<ConfigListener> listeners = configListeners.get(buildListenerKey(serviceName, label));
            if (listeners != null) {
                Iterator<ConfigListener> listenerIterator = listeners.iterator();
                while(listenerIterator.hasNext()) {
                    ConfigListener listener = listenerIterator.next();
                    if (listener.interest(key)) {
                        try {
                            listener.onChange(key, String.valueOf(value));
                        } catch (Throwable e) {
                            LOGGER.error("config listener onChange() failed", e);
                        }
                    }
                    /**
                     * 监听器回调失败次数超过LISTENER_MAX_FAILURE_TIMES，则认为是无效监听器，将会自动移除
                     */
                    if (listener instanceof FailureCounter) {
                        int count = ((FailureCounter) listener).countFailure();
                        if (count > LISTENER_MAX_FAILURE_TIMES) {
                            listenerIterator.remove();
                        }
                    }
                }
            }
        }
    }

    private void onChange(String serviceName, String label, Map<String, Object> old, Map.Entry<String, Object> entry) {
        onChange(serviceName, label, old, entry.getKey(), entry.getValue());
    }

    private void onChange(String serviceName, String label, Map<String, Object> old, Map<String, Object> configValue) {
        for (Map.Entry<String, Object> entry : configValue.entrySet()) {
            onChange(serviceName, label, old, entry);
        }
    }

    @Override
    public void publishConfigItem(String serviceName, String label, String key, String value) {
        ServiceConfig param = new ServiceConfig();
        param.setServiceCode(serviceName);
        param.setConfigVersion(label);
        ServiceConfig serviceConfig = configRepository.selectOne(param);

        /**
         * 该服务及标签所对应的配置文件不存在时，创建新的配置文件
         */
        if (serviceConfig == null ) {
            ServiceConfig config = new ServiceConfig();
            config.setServiceCode(serviceName);
            config.setConfigVersion(label);
            Map<String, String> configValue = new HashMap<>();
            configValue.put(key, value);
            config.setConfigValue(JSON.toJSONString(configValue));
            configRepository.insertOptional(config,
                    ServiceConfig.FIELD_SERVICE_CODE,
                    ServiceConfig.FIELD_CONFIG_VERSION,
                    ServiceConfig.FIELD_CONFIG_VALUE);
        } else {//否则把新的配置追加到原有的配置文件上
            @SuppressWarnings("unchecked")
            Map<String, Object> config = JSON.parseObject(serviceConfig.getConfigValue(), Map.class);
            /**
             * 旧配置会被覆盖，新配置会被追加
             */
            config.put(key, value);
            serviceConfig.setConfigValue(JSON.toJSONString(config));
            configRepository.updateByPrimaryKey(serviceConfig);
        }

        onChange(serviceName, label, serviceConfig == null ? null : convertProperties(serviceConfig.getConfigValue(), ConfigFileFormat.JSON), convertProperties(key + "=" + value, ConfigFileFormat.PROPERTIES));
    }

    @Override
    public void registerListener(String serviceName, String label, ConfigListener listener) {
        String key = buildListenerKey(serviceName, label);
        List<ConfigListener> listeners = configListeners.get(key);
        if (listeners == null) {
            listeners = Collections.synchronizedList(new ArrayList<>());
            listeners.add(listener);
            configListeners.putIfAbsent(key, listeners);
        } else {
            listeners.add(listener);
        }
    }

    private String buildListenerKey(String serviceName, String label) {
        return serviceName + "-" + label;
    }
}
