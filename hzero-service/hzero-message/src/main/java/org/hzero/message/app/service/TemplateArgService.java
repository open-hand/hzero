package org.hzero.message.app.service;

import org.hzero.message.domain.entity.TemplateArg;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息模板参数应用服务
 *
 * @author fanghan.liu@hand-china.com 2019-10-08 16:49:41
 */
public interface TemplateArgService {

    /**
     * 分页查询消息模板SQL参数
     *
     * @param templateId  模板ID
     * @param argName     参数名称
     * @param pageRequest 分页
     * @return 查询结果
     */
    Page<TemplateArg> pageTemplateArgs(Long templateId, String argName, PageRequest pageRequest);

    /**
     * 初始化消息模板参数
     *
     * @param templateId 模板ID
     * @param tenantId   租户ID
     */
    void initTemplateArgs(Long templateId, Long tenantId);

    /**
     * 修改模板参数多语言描述
     *
     * @param templateArg 消息模板参数
     * @return java.util.List<org.hzero.message.domain.entity.TemplateArg>
     */
    TemplateArg updateTemplateArgs(TemplateArg templateArg);
}
