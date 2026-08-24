package org.hzero.boot.platform.rule.helper;

import java.util.Map;

import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.service.RuleEngineService;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 运行脚本方法
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/30 11:02
 */
public class RuleEngineHelper {

    private RuleEngineHelper() {
    }

    public static ScriptResult runScript(String scriptCode, Long tenantId, Map<String, Object> params) {
        RuleEngineService ruleEngineService = ApplicationContextHelper.getContext().getBean(RuleEngineService.class);
        return ruleEngineService.ruleScriptResult(scriptCode, tenantId, params);
    }
}
