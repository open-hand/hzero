package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.mybatis.base.BaseRepository;

import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * 消息模板资源库
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
public interface MessageTemplateRepository extends BaseRepository<MessageTemplate> {
    /**
     * 分页查询消息模板
     *
     * @param tenantId                     租户ID
     * @param templateCode                 消息模板编码
     * @param templateName                 消息模板名称
     * @param enabledFlag                  启用标记
     * @param lang                         语言
     * @param includeSiteIfQueryByTenantId 按照租户ID检索的时候是否包含平台
     * @param pageRequest                  分页
     * @return 消息模板列表
     */
    Page<MessageTemplate> selectMessageTemplate(Long tenantId, String templateCode, String templateName, Integer enabledFlag, String lang, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 查询消息模板信息
     *
     * @param tenantId   租户Id
     * @param templateId 消息模板ID
     * @return 消息模板信息
     */
    MessageTemplate selectMessageTemplateByPrimaryKey(Long tenantId, long templateId);

    /**
     * 查询消息模板语言
     *
     * @param tenantId         租户ID
     * @param templateCodeList 模板编码列表
     * @return 模板语言列表
     */
    Set<String> listMessageTemplateLang(long tenantId, Set<String> templateCodeList);

    /**
     * 根据编码查询模板
     *
     * @param tenantId     租户id
     * @param templateCode 模板编码
     * @return 查询结果
     */
    MessageTemplate selectByCode(Long tenantId, String templateCode);

}
