package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.RuleScript;

/**
 * 规则脚本应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
public interface RuleScriptService {

    /**
     * 根据编码和租户查询
     *
     * @param scriptCode 脚本编码
     * @param tenantId   租户Id
     * @return 规则脚本
     */
    RuleScript selectRuleScriptByCode(String scriptCode, Long tenantId);

    /**
     * 新建
     *
     * @param ruleScript 规则脚本
     * @return 新建的对象
     */
    RuleScript createRuleScript(RuleScript ruleScript);

    /**
     * 更新
     *
     * @param ruleScript 规则脚本
     * @return 更新的对象
     */
    RuleScript updateRuleScript(RuleScript ruleScript);

    /**
     * 删除
     *
     * @param ruleScriptId 主键
     */
    void deleteRuleScript(Long ruleScriptId);
}
