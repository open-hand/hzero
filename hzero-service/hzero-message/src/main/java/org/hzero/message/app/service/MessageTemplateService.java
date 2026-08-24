package org.hzero.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.api.dto.LanguageDTO;
import org.hzero.message.domain.entity.MessageTemplate;

import java.util.List;
import java.util.Set;

/**
 * 消息模板应用服务
 *
 * @author qingsheng.chen@hand-china 2018-07-25 16:11:11
 */
public interface MessageTemplateService {
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
    Page<MessageTemplate> listMessageTemplate(Long tenantId, String templateCode, String templateName, Integer enabledFlag, String lang, boolean includeSiteIfQueryByTenantId, PageRequest pageRequest);

    /**
     * 查询消息模板
     *
     * @param tenantId   租户ID
     * @param templateId 消息模板ID
     * @return 消息模板
     */
    MessageTemplate getMessageTemplate(Long tenantId, long templateId);

    /**
     * 复制消息模板
     *
     * @param tenantId   租户ID
     * @param templateId 消息模板ID
     * @return 消息模板
     */
    MessageTemplate copyMessageTemplate(Long tenantId, Long templateId);

    /**
     * 查询消息模板
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 消息模板
     */
    MessageTemplate getMessageTemplate(long tenantId, String templateCode, String lang);

    /**
     * 查询消息模板语言
     *
     * @param tenantId    租户ID
     * @param messageCode 消息编码
     * @return 模板语言列表
     */
    List<LanguageDTO> listMessageTemplateLang(long tenantId, String messageCode);

    /**
     * 查询模板参数
     *
     * @param tenantId    租户Id
     * @param messageCode 消息编码
     * @param lang        语言
     * @return 参数列表
     */
    Set<String> listMessageTemplateArgs(long tenantId, String messageCode, String lang);

    /**
     * 创建消息模板
     *
     * @param messageTemplate 消息模板
     * @return 消息模板
     */
    MessageTemplate createMessageTemplate(MessageTemplate messageTemplate);

    /**
     * 更新消息模板
     *
     * @param messageTemplate 消息模板
     * @return 消息模板
     */
    MessageTemplate updateMessageTemplate(MessageTemplate messageTemplate);

    /**
     * 获取模板中的参数
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 模板参数
     */
    List<String> getTemplateArg(long tenantId, String templateCode, String lang);
}
