package org.hzero.message.domain.repository;

import java.util.List;

import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.mybatis.base.BaseRepository;

/**
 * <p>
 * 服务模板关联行
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 13:27
 */
public interface TemplateServerLineRepository extends BaseRepository<TemplateServerLine> {

    /**
     * 查询所有消息模板关联的服务
     *
     * @param tempServerId 消息模板服务ID
     * @param tenantId     租户Id
     * @return 消息模板账户列表
     */
    List<TemplateServerLine> listTemplateServerLine(long tempServerId, long tenantId);

    /**
     * 查询启用的消息模板关联的服务
     *
     * @param tempServerId 消息模板服务ID
     * @param tenantId     租户Id
     * @return 消息模板账户列表
     */
    List<TemplateServerLine> enabledTemplateServerLine(long tempServerId, long tenantId);

    /**
     * 查询模板服务关联行
     *
     * @param tenantId    租户ID
     * @param messageCode 消息代码
     * @param typeCode    模板类型
     * @return 模板服务关联行
     */
    TemplateServerLine getTemplateServerLine(long tenantId, String messageCode, String typeCode);
}
