package org.hzero.boot.platform.code.autoconfigure;

import org.hzero.boot.platform.code.CodeRuleClient;
import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.code.builder.DefaultCodeRuleBuilder;
import org.hzero.boot.platform.code.feign.CodeRuleRemoteService;
import org.hzero.boot.platform.code.impl.CodeRuleClientImpl;
import org.hzero.boot.platform.code.repository.CodeRuleDetailRepository;
import org.hzero.boot.platform.code.repository.DefaultCodeRuleDetailRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 编码规则自动注册类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/10 10:13
 */
@Configuration
@ComponentScan(basePackageClasses = CodeRuleRemoteService.class)
@EnableFeignClients(basePackageClasses = CodeRuleRemoteService.class)
@EnableConfigurationProperties(CodeRuleProperties.class)
public class CodeAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public CodeRuleDetailRepository codeRuleDetailRepository() {
        return new DefaultCodeRuleDetailRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public CodeRuleBuilder codeRuleBuilder() {
        return new DefaultCodeRuleBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public CodeRuleClient codeRuleClient(CodeRuleRemoteService codeRuleRemoteService) {
        return new CodeRuleClientImpl(codeRuleRemoteService);
    }
}
