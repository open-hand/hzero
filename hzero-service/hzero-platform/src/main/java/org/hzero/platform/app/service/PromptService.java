package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.Prompt;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * 多语言描述服务类
 * @author xianzhi.chen@hand-china.com	2018年8月9日下午3:11:11
 */
public interface PromptService {

    /**
     * 
     * 多语言描述导入
     * @param tenantId
     * @param promptFile
     */
    void promptImport(Long tenantId, MultipartFile promptFile);

    /**
     * 新建多语言描述
     *
     * @param prompt 新建或更新数据
     * @param organizationId 租户Id
     * @return 返回参数
     */
    Prompt insertPromptDescription(Prompt prompt, Long organizationId);

    /**
     * 按照条件删除多语言描述信息
     *
     * @param prompt 删除条件
     */
    void deletePromptByOptions(Prompt prompt);

    /**
     * 更新多语言描述
     *
     * @param prompt 新建或更新数据
     * @param organizationId 租户Id
     * @return 返回参数
     */
    Prompt updatePromptDescription(Prompt prompt, Long organizationId);
}
