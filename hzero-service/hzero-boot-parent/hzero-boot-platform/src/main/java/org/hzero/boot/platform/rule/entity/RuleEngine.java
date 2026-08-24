package org.hzero.boot.platform.rule.entity;

import java.util.Map;

/**
 * 规则引擎传输对象
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/29 11:17
 */
public class RuleEngine {

    private String script;
    private Map<String, Object> params;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "RuleEngine{" +
                "script='" + script + '\'' +
                ", params=" + params +
                '}';
    }
}
