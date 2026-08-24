package org.hzero.message.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.message.domain.entity.TemplateServer;

/**
 * 接收配置应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
public interface ReceiveConfigService {

    /**
     * 查询配置列表
     *
     * @param organizationId 租户Id
     * @return 配置
     */
    List<ReceiveConfigDTO> listConfig(Long organizationId);

    /**
     * 新建、更新
     *
     * @param receiveConfigList 接收配置
     * @param organizationId    租户ID
     * @return 接收配置
     */
    List<ReceiveConfig> createAndUpdate(List<ReceiveConfig> receiveConfigList, Long organizationId);

    /**
     * 删除接收配置
     *
     * @param receiveConfig 接收配置
     */
    void deleteConfig(ReceiveConfig receiveConfig);

    /**
     * 初始化接收配置
     *
     * @param templateServer 发送配置
     * @param typeCodes      消息发送类型
     */
    void initReceiveConfig(TemplateServer templateServer, Set<String> typeCodes);

    /**
     * 更新配置
     *
     * @param configCode 编码
     * @param tenantId   租户Id
     * @param typeList   消息类型
     */
    void updateReceiveConfig(String configCode, Long tenantId, List<String> typeList);

    /**
     * 删除配置
     *
     * @param configCode 编码
     * @param tenantId   租户Id
     */
    void deleteReceiveConfig(String configCode, Long tenantId);
}
