package org.hzero.message.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.WebhookServer;
import io.choerodon.mybatis.common.BaseMapper;
import java.util.List;

/**
 * webhook配置Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
public interface WebhookServerMapper extends BaseMapper<WebhookServer> {

    /**
     * 查询 webHook 列表数据
     *
     * @param webhookServer 查询条件
     * @param includeSiteIfQueryByTenantId 查询标识
     * @return 返回结果
     */
    List<WebhookServer> selectWebHookList(@Param("webhookServer") WebhookServer webhookServer,
                                          @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 查询 webHook 详情信息
     *
     * @param tenantId 租户Id
     * @param serverId 主键Id
     * @return 查询结果
     */
    WebhookServer selectWebHookDetails(@Param("tenantId") Long tenantId, @Param("serverId") Long serverId);
}
