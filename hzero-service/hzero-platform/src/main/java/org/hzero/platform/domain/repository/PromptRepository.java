package org.hzero.platform.domain.repository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.PromptDTO;
import org.hzero.platform.domain.entity.Prompt;

/**
 * 多语言描述资源库
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:18
 */
public interface PromptRepository extends BaseRepository<Prompt> {

    /**
     * 对多语言描述进行更新或者操作
     *
     * @param promptList 多语言描述list
     * @return 多语言描述list
     */
    List<Prompt> insertOrUpdate(List<Prompt> promptList);

    /**
     * 新增或者更新租户级多语言
     *
     * @param promptList 多语言list
     * @param tenantId 租户id
     * @return 多语言list
     */
    List<Prompt> insertOrUpdatePromptTenant(List<Prompt> promptList, Long tenantId);

    /**
     * 新增多语言描述，并同步redis
     *
     * @param prompt 平台级多语言描述
     * @return 平台级多语言描述
     */
    Prompt insertPrompt(Prompt prompt);

    /**
     * 更新多语言描述，并同步redis
     *
     * @param prompt 平台级多语言描述
     * @return 平台级多语言描述
     */
    Prompt updatePrompt(Prompt prompt);

    /**
     * 删除平台级多语言描述，并同步redis
     *
     * @param prompt 平台级多语言描述
     */
    void deletePrompt(Prompt prompt);

    /**
     * 删除租户级多语言
     * 
     * @param prompt 租户级多语言
     * @param tenantId 租户id
     */
    void deletePromptTenant(Prompt prompt, Long tenantId);

    /**
     * 获取多语言描述
     *
     * @param promptKey 多语言key
     * @param lang 语言
     * @param tenantId 租户id
     * @return 描述
     */
    Object getDescription(String[] promptKey, String lang, Long tenantId);

    /**
     * 查询多语言标签信息
     *
     * @param pageRequest 分页工具类
     * @param prompt 多语言标签信息
     * @return 多语言标签信息list
     */
    List<PromptDTO> selectPrompt(PageRequest pageRequest, Prompt prompt);

    /**
     * 查询租户级的多语言描述信息
     *
     * @param pageRequest 分页
     * @param prompt 多语言
     * @return 多语言dtoList
     */
    List<PromptDTO> selectPromptTenant(PageRequest pageRequest, Prompt prompt);

    /**
     * 
     * 查询单个多语言描述
     * @param prompt
     * @return
     */
    Prompt selectOnePrompt(Prompt prompt);

    /**
     * 分页查询当前用户语言环境下多语言信息列表
     *
     * @param pageRequest 分页参数
     * @param prompt 查询条件
     * @return Page<PromptDTO>
     */
    Page<PromptDTO> selectCurrentLangPrompts(PageRequest pageRequest, Prompt prompt);

    /**
     * 获取多语言明细信息
     *
     * @param prompt 查询条件
     * @return 查询结果
     */
    Prompt getPromptDetails(Prompt prompt);

    /**
     * 插入多语言信息
     *
     * @param prompts 数据集合
     * @param organizationId 租户Id
     */
    void insertPromptDescription(List<Prompt> prompts, Long organizationId);

    /**
     * 更新多语言信息
     *
     * @param prompts 数据集合
     * @param organizationId 租户Id
     */
    void updatePromptDescription(List<Prompt> prompts, Long organizationId);

    /**
     * 查询需缓存的多语言数据
     *
     * @return List<Prompt>
     */
    List<Prompt> selectCachePrompt();
}
