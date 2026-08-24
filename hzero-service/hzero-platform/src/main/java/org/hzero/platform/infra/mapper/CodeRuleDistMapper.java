package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.api.dto.CodeRuleDistDTO;
import org.hzero.platform.domain.entity.CodeRuleDist;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 编码规则分配mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:00
 */
public interface CodeRuleDistMapper extends BaseMapper<CodeRuleDist> {

    /**
     * 根据ruleId进行查询编码规则分配
     *
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配list
     */
    List<CodeRuleDistDTO> selectCodeRuleDistList(CodeRuleDist codeRuleDist);
}
