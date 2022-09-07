package org.hzero.message.app.service;

import org.hzero.message.domain.entity.DingTalkServer;

/**
 * 钉钉配置应用服务
 *
 * @author zifeng.ding@hand-china.com 2019-11-13 14:36:25
 */
public interface DingTalkServerService {

    /**
     * 更新
     *
     * @param dingTalkServer 钉钉配置
     * @return 更新的对象
     */
    DingTalkServer updateDingTalkServer(DingTalkServer dingTalkServer);

    /**
     * 创建
     *
     * @param dingTalkServer 钉钉配置
     * @return 创建的对象
     */
    DingTalkServer addDingTalkServer(DingTalkServer dingTalkServer);

    /**
     * 获取钉钉配置
     *
     * @param tenantId   租户ID
     * @param serverCode 钉钉编码
     * @return 钉钉配置
     */
    DingTalkServer detailDingTalkServer(Long tenantId, String serverCode);

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
     * 删除钉钉配置
     *
     * @param dingTalkServer 钉钉配置
     */
    void deleteDingTalkServer(DingTalkServer dingTalkServer);
}
