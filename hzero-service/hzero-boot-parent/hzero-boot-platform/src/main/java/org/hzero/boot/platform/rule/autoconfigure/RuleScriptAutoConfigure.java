package org.hzero.boot.platform.rule.autoconfigure;

import org.hzero.boot.platform.rule.controller.RuleEngineController;
import org.hzero.boot.platform.rule.feign.RemoteRuleScriptService;
import org.hzero.boot.platform.rule.helper.RuleScriptHelper;
import org.hzero.boot.platform.rule.service.RuleEngineService;
import org.hzero.boot.platform.rule.service.impl.RuleEngineServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 20:03
 */
@Configuration
@ComponentScan(basePackageClasses = RemoteRuleScriptService.class)
@EnableFeignClients(basePackageClasses = RemoteRuleScriptService.class)
public class RuleScriptAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public RuleScriptHelper ruleScriptHelper() {
        return new RuleScriptHelper();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleEngineService ruleEngineService() {
        return new RuleEngineServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public RuleEngineController ruleEngineController() {
        return new RuleEngineController();
    }
}