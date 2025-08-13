package org.hzero.config.client.api;

/**
 * 配置中心客户端通用接口
 * @author XCXCXCXCX
 * @date 2019/9/23
 */
public interface ConfigClient {

    /**
     * 推送整个配置文件，文件形式
     * @param env
     * @param serviceName
     * @param version
     * @param fileType
     * @param content
     */
    void publishConfig(String env, String serviceName, String version, String fileType, String content);

    /**
     * 推送单条配置，key｜value形式
     * @param env
     * @param serviceName
     * @param version
     * @param key
     * @param value
     */
    void publishConfigKeyValue(String env, String serviceName, String version, String key, String value);

}
