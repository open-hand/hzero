package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.RuleScript;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 规则脚本 Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
public interface RuleScriptMapper extends BaseMapper<RuleScript> {

    /**
     * 多条件查询
     *
     * @param ruleScript 规则脚本实体
     * @return 规则脚本集合
     */
    List<RuleScript> listRuleScript(RuleScript ruleScript);

    /**
     * 查询详情
     *
     * @param ruleScriptId 主键
     * @param tenantId     租户ID
     * @return 规则脚本
     */
    RuleScript selectRuleScriptById(@Param("ruleScriptId") Long ruleScriptId,
                                    @Param("tenantId") Long tenantId);

    /**
     * 根据编码查询
     *
     * @param scriptCode 编码
     * @param tenantId   租户Id
     * @return 规则脚本
     */
    RuleScript selectRuleScriptByCode(@Param("scriptCode") String scriptCode,
                                      @Param("tenantId") Long tenantId);
}
