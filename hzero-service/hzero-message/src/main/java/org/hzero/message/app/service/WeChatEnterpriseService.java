package org.hzero.message.app.service;

import org.hzero.message.domain.entity.WeChatEnterprise;

/**
 * 企业微信配置应用服务
 *
 * @author fanghan.liu@hand-china.com 2019-10-15 14:31:46
 */
public interface WeChatEnterpriseService {

    /**
     * 更新企业微信配置
     *
     * @param weChatEnterprise 企业微信配置
     * @return 企业微信配置
     */
    WeChatEnterprise updateWeChatEnterprise(WeChatEnterprise weChatEnterprise);

    /**
     * 新增企业微信配置
     *
     * @param weChatEnterprise 企业微信配置
     * @return 企业微信配置
     */
    WeChatEnterprise createWeChatEnterprise(WeChatEnterprise weChatEnterprise);

    /**
     * 新增企业微信配置
     *
     * @param tenantId   租户ID
     * @param serverCode 企业微信编码
     * @return 企业微信配置
     */
    WeChatEnterprise detailWeChatEnterprise(Long tenantId, String serverCode);

    /**
     * 获取解密后的配置信息
     *
     * @param tenantId   租户
     * @param serverCode 配置编码
     * @return token
     */
    String getToken(Long tenantId, String serverCode);

    /**
     * 获取默认应用ID
     *
     * @param tenantId   租户
     * @param serverCode 配置编码
     * @return agentId
     */
    Long getDefaultAgentId(Long tenantId, String serverCode);

    /**
     * 删除企业微信配置
     *
     * @param weChatEnterprise 企业微信配置
     */
    void deleteWeChatEnterprise(WeChatEnterprise weChatEnterprise);
}
