package org.hzero.platform.infra.mapper;


import java.util.List;

import org.hzero.platform.api.dto.PromptDTO;
import org.hzero.platform.domain.entity.Prompt;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 多语言描述Mapper
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:18
 */
public interface PromptMapper extends BaseMapper<Prompt> {

    /**
     * 查询多语言标签信息
     *
     * @param prompt 多语言标签信息
     * @return 多语言标签信息list
     */
    List<PromptDTO> selectPrompt(Prompt prompt);

    /**
     * 查询租户级的多语言描述信息
     *
     * @param prompt 多语言
     * @return 多语言dtoList
     */
    List<PromptDTO> selectPromptTenant(Prompt prompt);

    /**
     * 
     * 查询单个多语言描述对象
     * 
     * @param prompt
     * @return
     */
    Prompt selectOnePrompt(Prompt prompt);

    /**
     * 查询缓存的多语言数据
     *
     * @return
     */
    List<Prompt> selectCachePrompt();
}
