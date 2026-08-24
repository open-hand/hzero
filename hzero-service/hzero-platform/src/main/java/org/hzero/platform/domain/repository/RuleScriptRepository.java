package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.RuleScript;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 规则脚本 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
public interface RuleScriptRepository extends BaseRepository<RuleScript> {

    /**
     * 分页查询
     *
     * @param pageRequest 分页
     * @param ruleScript  规则脚本实体
     * @return 规则脚本分页数据
     */
    Page<RuleScript> pageRuleScript(PageRequest pageRequest, RuleScript ruleScript);

    /**
     * 查询详情
     *
     * @param ruleScriptId 主键
     * @param tenantId     租户Id
     * @return 规则脚本
     */
    RuleScript selectRuleScript(Long ruleScriptId, Long tenantId);

    /**
     * 根据code查询
     *
     * @param scriptCode 规则编码
     * @param tenantId   租户Id
     * @return 规则脚本
     */
    RuleScript selectRuleScriptByCode(String scriptCode, Long tenantId);
}
