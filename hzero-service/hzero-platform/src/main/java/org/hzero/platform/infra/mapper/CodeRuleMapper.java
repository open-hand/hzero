package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.CodeRuleDTO;
import org.hzero.platform.domain.entity.CodeRule;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 编码规则mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 17:56
 */
public interface CodeRuleMapper extends BaseMapper<CodeRule> {

    /**
     * 查询编码规则dtoList
     *
     * @param codeRule 编码规则
     * @return 编码规则dtoList
     */
    List<CodeRuleDTO> selectCodeRuleList(CodeRule codeRule);

    /**
     * 通过编码规则明细ID查询编码规则是否存在
     *
     * @param ruleDetailId 编码规则明细ID
     * @return 编码规则
     */
    CodeRule selectCodeRuleByDetailId(@Param("ruleDetailId") Long ruleDetailId);

    List<CodeRuleDTO> selectCodeRuleListByCodes(@Param("tenantId") Long tenantId, @Param("ruleCodeList") List<String> ruleCodeList);
}
