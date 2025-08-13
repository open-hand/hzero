package org.hzero.message.infra.mapper;


import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.TemplateServer;

import java.util.List;

/**
 * 消息模板账户Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-09-07 11:09:29
 */
public interface TemplateServerMapper extends BaseMapper<TemplateServer> {
    /**
     * 查询消息模板信息
     *
     * @param tenantId                     租户ID
     * @param messageCode                  消息代码
     * @param messageName                  消息名称
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 消息模板账户列表
     */
    List<TemplateServer> selectTemplateServer(@Param("tenantId") Long tenantId,
                                              @Param("messageCode") String messageCode,
                                              @Param("messageName") String messageName,
                                              @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 查询消息模板信息
     *
     * @param tenantId     租户ID
     * @param tempServerId ID
     * @return 消息模板账户
     */
    TemplateServer getTemplateServer(@Param("tenantId") Long tenantId,
                                     @Param("tempServerId") long tempServerId);
}
