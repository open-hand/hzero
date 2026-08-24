package org.hzero.message.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.MessageTemplate;

import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 消息模板Mapper
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
public interface MessageTemplateMapper extends BaseMapper<MessageTemplate> {
    /**
     * 查询消息模板
     *
     * @param tenantId                     租户ID
     * @param templateCode                 消息模板编码
     * @param templateName                 消息模板名称
     * @param enabledFlag                  启用标记
     * @param lang                         语言
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @return 消息模板列表
     */
    List<MessageTemplate> selectMessageTemplate(@Param("tenantId") Long tenantId,
                                                @Param("templateCode") String templateCode,
                                                @Param("templateName") String templateName,
                                                @Param("enabledFlag") Integer enabledFlag,
                                                @Param("lang") String lang,
                                                @Param("includeSiteIfQueryByTenantId") boolean includeSiteIfQueryByTenantId);

    /**
     * 查询消息模板信息
     *
     * @param tenantId   租户Id
     * @param templateId 消息模板ID
     * @return 消息模板信息
     */
    MessageTemplate selectMessageTemplateByPrimaryKey(@Param("tenantId") Long tenantId,
                                                      @Param("templateId") long templateId);

    /**
     * 查询消息模板语言
     *
     * @param tenantId         租户ID
     * @param templateCodeList 模板编码列表
     * @return 模板语言列表
     */
    Set<String> listMessageTemplateLang(@Param("tenantId") long tenantId, @Param("templateCodeList") Set<String> templateCodeList);

    /**
     * 更新
     *
     * @param id 主键
     * @return 更新数量
     */
    int updateSelf(@Param("id") long id);

    /**
     * 根据编码查询模板
     *
     * @param tenantId     租户id
     * @param templateCode 模板编码
     * @return 查询结果
     */
    List<MessageTemplate> selectByCode(@Param("tenantId") Long tenantId,
                                       @Param("templateCode") String templateCode);

}
