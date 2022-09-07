package org.hzero.message.app.service;

import java.util.List;

import org.hzero.message.api.dto.UserReceiveConfigDTO;
import org.hzero.message.domain.entity.UserReceiveConfig;

/**
 * 用户接收配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface UserReceiveConfigService {

    /**
     * 查询
     *
     * @param userId         用户Id
     * @param organizationId 租户ID
     * @return 用户配置
     */
    List<UserReceiveConfigDTO> listUserConfig(Long userId, Long organizationId);

    /**
     * 创建及修改
     *
     * @param organizationId        租户Id
     * @param userReceiveConfigList 用户配置
     * @return 用户配置
     */
    List<UserReceiveConfigDTO> createAndUpdate(Long organizationId, List<UserReceiveConfig> userReceiveConfigList);

    /**
     * 刷新租户下用户接收配置
     *
     * @param tenantId 租户ID
     */
    void refreshTenantUserConfig(Long tenantId);

    /**
     * 刷新所有用户接收配置
     */
    void refreshAllUserConfig();
}
