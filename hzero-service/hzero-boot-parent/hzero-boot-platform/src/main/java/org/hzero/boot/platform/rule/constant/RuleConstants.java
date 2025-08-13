package org.hzero.boot.platform.rule.constant;

/**
 * 规则引擎常量类
 *
 * @author shuangfei.zhu@hand-china 2018/09/29 11:00
 */
public class RuleConstants {

    private RuleConstants(){}

    public static final String REQUEST_MAPPING = "/v1/rule-engine";

    public static final String POST_MAPPING = "/run-script";

    public static final class ErrorCode{
        private ErrorCode(){}
        /**
         * 业务脚本校验不通过
         */
        public static final String RULE_SCRIPT_FAIL = "hpfm.error.rule.script.check";
        /**
         * 获取规则引擎配置失败
         */
        public static final String RULE_SCRIPT_GET_RULE_FAILED = "hpfm.error.rule_script.get_rule_failed";
        /**
         * Groovy脚本运行失败
         */
        public static final String RULE_SCRIPT_GET_GROOVY_FAILED = "hpfm.error.rule_script.run_groovy_failed";   
        /**
         * 未找到指定服务
         */
        public static final String RULE_SCRIPT_SERVCIE_NOT_FOUND = "hpfm.error.rule_script.service_not_found";
                        
        
    }
}
