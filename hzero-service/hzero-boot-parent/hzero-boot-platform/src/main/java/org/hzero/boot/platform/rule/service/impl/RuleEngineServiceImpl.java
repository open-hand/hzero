package org.hzero.boot.platform.rule.service.impl;

import java.util.Map;
import java.util.Objects;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.GroovyInterceptor;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.boot.platform.rule.constant.RuleConstants;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.service.RuleEngineService;

/**
 * 应用服务实现
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/28 20:50
 */
public class RuleEngineServiceImpl implements RuleEngineService {

    private static Logger logger = LoggerFactory.getLogger(RuleEngineServiceImpl.class);

    private static final String EXIT = "exit";

    @Override
    public ScriptResult ruleScriptResult(String scriptCode, Long tenantId, Map<String, Object> params) {
        throw new UnsupportedOperationException();
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
