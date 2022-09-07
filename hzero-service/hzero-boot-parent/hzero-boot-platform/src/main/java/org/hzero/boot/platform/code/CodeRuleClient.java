package org.hzero.boot.platform.code;

import org.hzero.boot.platform.code.entity.CodeRule;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.CodeRuleDist;

/**
 * <p>
 * 编码规则客户端
 * </p>
 *
 * @author qingsheng.chen 2019/3/5 星期二 14:32
 */
public interface CodeRuleClient {
    /**
     * 获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId   租户ID
     * @param codeRuleId 编码规则ID
     * @return 获取编码规则、编码规则分配、编码规则明细
     */
    CodeRule getCodeRule(long tenantId, long codeRuleId);

    /**
     * 获取编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param ruleCode 编码规则编码
     * @return 获取编码规则、编码规则分配、编码规则明细
     */
    CodeRule getCodeRule(long tenantId, String ruleCode);

    /**
     * 租户新增(如果编码规则ID为null)/更新(如果编码规则ID不为null)编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param codeRule 编码规则明细
     * @return 保存编码规则
     */
    CodeRule saveCodeRule(long tenantId, CodeRule codeRule);

    /**
     * 保存编码规则分配、编码规则明细
     *
     * @param tenantId     租户ID
     * @param codeRuleDist 编码规则分配
     * @return 编码规则分配
     */
    CodeRuleDist saveCodeRuleDist(long tenantId, CodeRuleDist codeRuleDist);

    /**
     * 保存编码规则明细
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     * @return 编码规则明细
     */
    CodeRuleDetail saveCodeRuleDetail(long tenantId, CodeRuleDetail codeRuleDetail);

    /**
     * 删除编码规则、编码规则分配、编码规则明细
     *
     * @param tenantId 租户ID
     * @param codeRule 编码规则
     */
    void deleteCodeRule(long tenantId, CodeRule codeRule);

    /**
     * 删除编码规则分配、编码规则明细
     *
     * @param tenantId     租户ID
     * @param codeRuleDist 编码规则分配
     */
    void deleteCodeRuleDist(long tenantId, CodeRuleDist codeRuleDist);

    /**
     * 删除编码规则明细
     *
     * @param tenantId       租户ID
     * @param codeRuleDetail 编码规则明细
     */
    void deleteCodeRuleDetail(long tenantId, CodeRuleDetail codeRuleDetail);
}
