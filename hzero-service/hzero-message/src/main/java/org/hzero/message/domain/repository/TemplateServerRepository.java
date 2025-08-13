package org.hzero.message.domain.repository;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.mybatis.base.BaseRepository;


/**
 * 消息模板账户资源库
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
public interface TemplateServerRepository extends BaseRepository<TemplateServer> {

    /**
     * 分页查询消息模板信息
     *
     * @param tenantId                     租户ID
     * @param messageCode                  消息代码
     * @param messageName                  消息名称
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @param pageRequest                  分页
     * @return 消息模板账户列表
     */
    Page<TemplateServer> pageTemplateServer(Long tenantId, String messageCode, String messageName, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 查询消息模板信息
     *
     * @param tenantId     租户ID
     * @param tempServerId ID
     * @return 消息模板账户
     */
    TemplateServer getTemplateServer(Long tenantId, long tempServerId);
}
