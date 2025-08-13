package org.hzero.boot.platform.rule.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.hzero.boot.platform.rule.constant.RuleConstants;
import org.hzero.boot.platform.rule.entity.RuleEngine;
import org.hzero.boot.platform.rule.entity.RuleScriptVO;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.helper.RuleScriptHelper;
import org.hzero.boot.platform.rule.service.RuleEngineService;
import org.hzero.core.base.BaseConstants;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.exception.CommonException;

/**
 * 应用服务实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 20:50
 */
public class RuleEngineServiceImpl implements RuleEngineService {
    private static Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

    private static final String EXIT = "exit";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RuleScriptHelper ruleScriptHelper;

    /**
     * Hzero平台HTTP协议,默认http
     */
    @Value("${hzero.platform.httpProtocol:http}")
    private String hzeroPlatformHttpProtocol;

    @Override
    public ScriptResult ruleScriptResult(String scriptCode, Long tenantId, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>(0);
        }
        RuleScriptVO rule = ruleScriptHelper.getRuleScript(tenantId, scriptCode);
        Assert.notNull(rule, RuleConstants.ErrorCode.RULE_SCRIPT_GET_RULE_FAILED);
        Assert.notNull(rule.getServerName(), RuleConstants.ErrorCode.RULE_SCRIPT_GET_RULE_FAILED);
        Assert.isTrue(Objects.equals(BaseConstants.Flag.YES, rule.getEnabledFlag()) && StringUtils.isNotBlank(rule.getScriptContent()),
                RuleConstants.ErrorCode.RULE_SCRIPT_GET_RULE_FAILED);
        String serverName = rule.getServerName();
        String script = rule.getScriptContent();

        try {
            // 请求对应服务上的接口执行脚本
            String url = this.hzeroPlatformHttpProtocol + "://" + serverName + RuleConstants.REQUEST_MAPPING + RuleConstants.POST_MAPPING;
            RuleEngine ruleEngine = new RuleEngine();
            ruleEngine.setScript(script);
            ruleEngine.setParams(params);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<RuleEngine> entity = new HttpEntity<>(ruleEngine, headers);

            ResponseEntity<ScriptResult> response = this.restTemplate.postForEntity(url, entity, ScriptResult.class);
            return response.getBody();
        } catch (Exception e) {
            throw new CommonException(RuleConstants.ErrorCode.RULE_SCRIPT_SERVCIE_NOT_FOUND, e);
        }
    }

    @Override
    public ScriptResult runGroovyScript(String script, Map<String, Object> params) {
        logger.debug(">>>>>>>>>>> Groovy script start running <<<<<<<<<<<");
        ScriptResult result = new ScriptResult();
        try {
            Binding binding = new Binding();
            GroovyShell shell = new GroovyShell(binding, new CompilerConfiguration().addCompilationCustomizers(new SandboxTransformer()));
            params.forEach(binding::setVariable);
            new NoSystemExitSandbox().register();
            new NoRunTimeSandbox().register();
            Object object = shell.evaluate(script);
            result.setFailed(false);
            result.setContent(object);
            logger.debug(">>>>>>>>>>> Groovy script running success <<<<<<<<<<<");
            return result;
        } catch (Exception e) {
            logger.warn(">>>>>>>>>>> Groovy script running failed <<<<<<<<<<<");
            result.setFailed(true);
            result.setContent(e.getMessage());
            return result;
        } catch (AssertionError e) {
            result.setFailed(true);
            result.setContent(RuleConstants.ErrorCode.RULE_SCRIPT_FAIL);
            return result;
        }
    }

    static class NoSystemExitSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == System.class && Objects.equals(method, EXIT)) {
                throw new SecurityException("No call on System.exit() please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }

    static class NoRunTimeSandbox extends GroovyInterceptor {
        @Override
        public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
            if (receiver == Runtime.class) {
                throw new SecurityException("No call on RunTime please");
            }
            return super.onStaticCall(invoker, receiver, method, args);
        }
    }
}
