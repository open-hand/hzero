package org.hzero.config.domain.service.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hzero.config.domain.service.PullConfigService;
import org.hzero.config.domain.vo.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathCompositeEnvironmentRepository;

/**
 * 默认config server从git拉取配置
 * 修改为从数据库获取配置
 *
 * @author youbin.wu
 */
public class DbEnvironmentRepository extends SearchPathCompositeEnvironmentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbEnvironmentRepository.class);

    @Value("${spring.application.name:hzero-config}")
    private String applicationName;

    private PullConfigService pullConfigService;

    private Map<String, HashMap<String, Environment>> cache = new ConcurrentHashMap<>();

    public DbEnvironmentRepository(List<EnvironmentRepository> environmentRepositories) {
        super(environmentRepositories);
    }

    public void setPullConfigService(PullConfigService pullConfigService) {
        this.pullConfigService = pullConfigService;
    }

    @Override
    public Locations getLocations(String application, String profile, String label) {
        return new Locations(application, profile, label, null, new String[0]);
    }

    @Override
    public Environment findOne(String application, String profile, String label) {
        String[] profiles = new String[1];
        profiles[0] = profile;
        Environment env;
        String info = label == null ? application : application + "-" + label;
        try {
            LOGGER.info("{} 获取配置", info);
            Config config = pullConfigService.getConfig(application, label);
            Map<String, Object> configMap = getDefaultConfig(config.getValue());
            PropertySource propertySource = new PropertySource(application + "-" + profile + "-" + label, configMap);
            String version = config.getConfigVersion();
            env = new Environment(applicationName, profiles, label, version, null);
            env.add(propertySource);
            setCache(application, label, env);
        } catch (Exception e) {
            if (getCache(application, label) != null) {
                env = getCache(application, label);
                LOGGER.warn("获取配置失败，返回上次缓存的配置. info {}", info, e);
            } else {
                throw e;
            }
        }
        return env;
    }

    private void setCache(String application, String label, Environment env) {
        this.cache.computeIfAbsent(application, key -> new HashMap<>(16))
                .putIfAbsent(label, env);
    }

    private Environment getCache(String application, String label) {
        if (this.cache.get(application) != null && this.cache.get(application).get(label) != null) {
            return this.cache.get(application).get(label);
        }
        return null;
    }

    private Map<String, Object> getDefaultConfig(Map<String, Object> map) {
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        map.put("spring.cloud.config.allowOverride", true);
        map.put("spring.cloud.config.failFast", false);
        map.put("spring.cloud.config.overrideNone", false);
        map.put("spring.cloud.config.overrideSystemProperties", false);
        map.put("spring.sleuth.integration.enabled", false);
        map.put("spring.sleuth.scheduled.enabled", false);
        map.put("sampler.percentage", 1.0);
        return map;
    }
}