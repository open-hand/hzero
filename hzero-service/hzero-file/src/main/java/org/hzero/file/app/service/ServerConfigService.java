package org.hzero.file.app.service;

import org.hzero.file.domain.entity.ServerConfig;

/**
 * 服务器上传配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
public interface ServerConfigService {

    /**
     * 服务器上传配置明细
     *
     * @param tenantId 租户ID
     * @param configId 服务器上传配置Id
     * @return 新建的配置
     */
    ServerConfig detailConfig(Long tenantId, Long configId);

    /**
     * 创建服务器上传配置
     *
     * @param serverConfig 服务器上传配置
     * @return 新建的配置
     */
    ServerConfig createConfig(ServerConfig serverConfig);

    /**
     * 更新服务器上传配置
     *
     * @param serverConfig 服务器上传配置
     * @return 更新的配置
     */
    ServerConfig updateConfig(ServerConfig serverConfig);
}
