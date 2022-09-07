package org.hzero.boot.platform.rule.controller;

import java.util.Map;

import org.hzero.boot.platform.rule.constant.RuleConstants;
import org.hzero.boot.platform.rule.entity.RuleEngine;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.service.RuleEngineService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.swagger.annotation.Permission;

/**
 * 规则引擎
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/29 9:11
 */
@RestController
@RequestMapping(RuleConstants.REQUEST_MAPPING)
public class RuleEngineController extends BaseController {

    @Autowired
    private RuleEngineService ruleEngineService;

    @Permission(permissionLogin = true)
    @PostMapping(RuleConstants.POST_MAPPING)
    public ResponseEntity<ScriptResult> runGroovyScript(@RequestBody RuleEngine ruleEngine) {
        String script = ruleEngine.getScript();
        Map<String, Object> params = ruleEngine.getParams();
        params.put("ApplicationContext", ApplicationContextHelper.getContext());
        return Results.success(ruleEngineService.runGroovyScript(script, params));
    }
}