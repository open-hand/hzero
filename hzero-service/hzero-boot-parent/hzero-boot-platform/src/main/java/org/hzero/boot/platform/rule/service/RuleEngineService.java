package org.hzero.boot.platform.rule.service;

import java.util.Map;

import org.hzero.boot.platform.rule.entity.ScriptResult;

/**
 * 应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 20:48
 */
public interface RuleEngineService {

    /**
     * 获取规则引擎定义的脚本的执行结果
     *
     * @param scriptCode 脚本编码
     * @param tenantId   租户Id
     * @param params     参数
     * @return 执行结果
     */
    ScriptResult ruleScriptResult(String scriptCode, Long tenantId, Map<String, Object> params);

    /**
     * 执行groovy脚本
     *
     * @param script 脚本内容
     * @param params 参数
     * @return 运行结果
     */
    ScriptResult runGroovyScript(String script, Map<String, Object> params);
}