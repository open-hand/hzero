package org.hzero.config.app.service;


import org.hzero.config.domain.vo.Config;

import java.util.Map;

/**
 * 服务配置应用服务
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
public interface ServiceConfigService {

    /**
     * 查询配置
     *
     * @param serviceName 服务ID
     * @param label 标签，未启用服务治理时，可传入版本号；启用服务治理时传入 @ 符号分隔的产品编码等信息
     * @return Config
     */
    Config selectConfig(String serviceName, String label);

    /**
     * 查询配置 json结构
     *
     * @param serviceName 服务ID
     * @param label 标签，未启用服务治理时，可传入版本号；启用服务治理时传入 @ 符号分隔的产品编码等信息
     * @return Config
     */
    Map<String, Object> getConfig(String serviceName, String label);

    /**
     * 发布配置
     * 配置不存在则生成新配置，配置存在则追加到原有配置
     * @param serviceName
     * @param label
     * @param fileType
     * @param content
     */
    void publishConfig(String serviceName, String label, String fileType, String content);

    /**
     * 发布配置项
     * @param serviceName
     * @param label
     * @param key
     * @param value
     */
    void publishConfigItem(String serviceName, String label, String key, String value);

    /**
     * 注册监听器
     * @param serviceName
     * @param label
     * @param listener
     */
    void registerListener(String serviceName, String label, ConfigListener listener);

}
