package org.hzero.admin.infra.util.config;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 配置文件构建器工厂
 *
 * @author wuguokai
 */
class BuilderFactory {
    private static Map<ConfigFileFormat, Supplier<Builder>> builderFactorys = new EnumMap<>(ConfigFileFormat.class);

    static {
        builderFactorys.put(ConfigFileFormat.YAML, YamlBuilder::new);
        builderFactorys.put(ConfigFileFormat.YML, YamlBuilder::new);
        builderFactorys.put(ConfigFileFormat.PROPERTIES, PropertiesBuilder::new);
    }

    private BuilderFactory() {
    }

    /**
     * 根据文件类型返回对应的构建器
     *
     * @param configFileFormat 文件枚举类型
     * @return Builder
     */
     static Builder getBuilder(ConfigFileFormat configFileFormat) {
        return Optional.ofNullable(builderFactorys.get(configFileFormat))
                .map(Supplier::get)
                .orElseThrow(() -> new IllegalArgumentException("当前仅支持yml、proprerties文件"));
    }
}