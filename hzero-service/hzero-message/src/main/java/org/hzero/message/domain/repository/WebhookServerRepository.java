package org.hzero.message.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.message.domain.entity.WebhookServer;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * webhook配置资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
public interface WebhookServerRepository extends BaseRepository<WebhookServer> {

    /**
     * 分页查询 webHook 参数
     *
     * @param pageRequest                  分页参数
     * @param webhookServer                查询条件
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 返回结果
     */
    Page<WebhookServer> pageWebHookList(PageRequest pageRequest, WebhookServer webhookServer, boolean includeSiteIfQueryByTenantId);

    /**
     * 获取webHookDetails
     *
     * @param serverId 主键Id
     * @param tenantId 租户Id
     * @return webhook详情结果
     */
    WebhookServer getWebHookDetails(Long tenantId, Long serverId);

    /**
     * 校验webHook参数唯一性
     *
     * 1. 单一租户下code唯一
     * 2. 单一租户下相同的webHook类型下地址不可重复
     *
     * @param webhookServer 校验参数
     */
    void validWebHookParams(WebhookServer webhookServer);

    /**
     * 获取唯一webhook
     *
     * @param tenantId 租户Id
     * @param serverCode 服务编码
     * @return WebhookServer
     */
    WebhookServer getWebHookByCodeAndTenant(Long tenantId, String serverCode);
}
