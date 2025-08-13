package org.hzero.message.app.service;

import org.hzero.message.domain.entity.WebhookServer;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * webhook配置应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
public interface WebhookServerService {

    /**
     * 分页查询 webHook 参数
     *
     * @param pageRequest   分页参数
     * @param webhookServer 查询条件
     * @param tenantId      租户Id
     * @param tenantId      按照租户ID检索的时候是否包含平台
     * @return 返回结果
     */
    Page<WebhookServer> pageWebHookList(PageRequest pageRequest, WebhookServer webhookServer, Long tenantId, boolean includeSiteIfQueryByTenantId);

    /**
     * 获取webHookDetails
     *
     * @param serverId 主键Id
     * @param tenantId 租户Id
     * @return webhook 详情结果
     */
    WebhookServer getWebHookDetails(Long tenantId, Long serverId);

    /**
     * 创建webhook配置
     *
     * @param tenantId      租户ID
     * @param webhookServer 新建参数
     * @return 创建结果
     */
    WebhookServer createWebHook(Long tenantId, WebhookServer webhookServer);

    /**
     * 更新webhook配置
     *
     * @param tenantId      租户ID
     * @param webhookServer 更新参数
     * @return 更新结果
     */
    WebhookServer updateWebHook(Long tenantId, WebhookServer webhookServer);

    /**
     * 删除webhook配置
     *
     * @param webhookServer 要删除的参数
     * @return 删除结果
     */
    void removeWebHook(WebhookServer webhookServer);
}
