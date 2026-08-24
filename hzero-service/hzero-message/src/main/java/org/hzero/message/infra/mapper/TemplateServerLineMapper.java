package org.hzero.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.TemplateServerLine;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 模板服务映射行
 * </p>
 *
 * @author qingsheng.chen 2018/9/30 星期日 13:07
 */
public interface TemplateServerLineMapper extends BaseMapper<TemplateServerLine> {
    /**
     * 查询消息模板关联的服务
     *
     * @param tempServerId 消息模板ID
     * @param tenantId     租户Id
     * @return 消息模板账户列表
     */
    List<TemplateServerLine> selectTemplateServerLine(@Param("tempServerId") long tempServerId,
                                                      @Param("tenantId") long tenantId);

    /**
     * 查询启用的消息模板关联的服务
     *
     * @param tempServerId 消息模板服务ID
     * @param tenantId     租户Id
     * @return 消息模板账户列表
     */
    List<TemplateServerLine> enabledTemplateServerLine(@Param("tempServerId") long tempServerId,
                                                       @Param("tenantId") long tenantId);

    /**
     * 查询模板服务关联行
     *
     * @param tenantId    租户ID
     * @param messageCode 消息代码
     * @param typeCode    模板类型(只能是EMAIL或者SMS)
     * @return 模板服务关联行
     */
    TemplateServerLine getTemplateServerLine(@Param("tenantId") long tenantId,
                                             @Param("messageCode") String messageCode,
                                             @Param("typeCode") String typeCode);
}
