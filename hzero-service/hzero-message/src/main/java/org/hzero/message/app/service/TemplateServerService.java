package org.hzero.message.app.service;

import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板账户应用服务
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
public interface TemplateServerService {
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
     * 查询所有消息模板关联的服务
     *
     * @param tempServerId 消息模板服务ID
     * @param tenantId     租户Id
     * @return 消息模板账户列表
     */
    List<TemplateServerLine> listTemplateServerLine(long tempServerId, long tenantId);

    /**
     * 查询消息模板信息
     *
     * @param tenantId    租户ID
     * @param messageCode 消息代码
     * @return 消息模板账户
     */
    TemplateServer getTemplateServer(long tenantId, String messageCode);

    /**
     * 查询消息模板信息
     *
     * @param tenantId     租户ID
     * @param tempServerId 消息发送配置ID
     * @return 消息模板账户
     */
    TemplateServer getTemplateServer(Long tenantId, long tempServerId);

    /**
     * 复制消息发送配置
     *
     * @param tenantId     租户ID
     * @param tempServerId 消息发送配置ID
     * @return 消息模板账户
     */
    TemplateServer copyTemplateServer(Long tenantId, Long tempServerId);

    /**
     * 查询服务编码
     *
     * @param tenantId    租户ID
     * @param typeCode    消息类型
     * @param messageCode 消息代码
     * @return serverCode 服务信息
     */
    TemplateServerLine getTemplateServerLine(long tenantId, String typeCode, String messageCode);

    /**
     * 查询模板服务行
     *
     * @param tenantId    租户ID
     * @param messageCode 消息代码
     * @return 模板服务行
     */
    List<TemplateServerLine> listTemplateServerLine(long tenantId, String messageCode);

    /**
     * 插入消息模板信息
     *
     * @param templateServer 模板信息
     */
    void createTemplateServer(TemplateServer templateServer);

    /**
     * 更新消息模板信息
     *
     * @param templateServer 模板信息
     */

    void updateTemplateServer(TemplateServer templateServer);

    /**
     * 删除消息模板信息
     *
     * @param templateServer 模板服务
     */
    void deleteTemplateServer(TemplateServer templateServer);

    /**
     * 删除消息模板服务信息行
     *
     * @param tempServerLineId   模板服务行Id
     * @param templateServerLine 模板服务行
     */
    void deleteTemplateServerLine(long tempServerLineId, TemplateServerLine templateServerLine);

}
