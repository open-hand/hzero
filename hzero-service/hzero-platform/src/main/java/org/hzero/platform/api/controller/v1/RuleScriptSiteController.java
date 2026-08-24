package org.hzero.platform.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.platform.rule.entity.ScriptResult;
import org.hzero.boot.platform.rule.helper.RuleEngineHelper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.RuleScriptTestDTO;
import org.hzero.platform.app.service.RuleScriptService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.RuleScript;
import org.hzero.platform.domain.repository.RuleScriptRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 规则脚本 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
@Api(tags = PlatformSwaggerApiConfig.RULE_SCRIPT_CONFIG_SITE)
@RestController("ruleScriptSiteController.v1")
@RequestMapping("/v1/rule-scripts")
public class RuleScriptSiteController extends BaseController {

    @Autowired
    private RuleScriptRepository ruleScriptRepository;

    @Autowired
    private RuleScriptService ruleScriptService;

    @ApiOperation(value = "规则脚本列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<RuleScript>> pageRuleScript(RuleScript ruleScript,
                                                           @ApiIgnore @SortDefault(value = RuleScript.FIELD_RULE_SCRIPT_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        Page<RuleScript> list = ruleScriptRepository.pageRuleScript(pageRequest, ruleScript);
        return Results.success(list);
    }

    @ApiOperation(value = "规则脚本明细")
    @Permission(level = ResourceLevel.SITE)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{ruleScriptId}")
    public ResponseEntity<RuleScript> detailRuleScript(@PathVariable @ApiParam(value = "规则脚本ID", required = true) @Encrypt Long ruleScriptId) {
        RuleScript ruleScript = ruleScriptRepository.selectRuleScript(ruleScriptId, null);
        return Results.success(ruleScript);
    }

    @ApiOperation(value = "测试规则脚本运行")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/test")
    public ResponseEntity<ScriptResult> runScript(@RequestBody RuleScriptTestDTO ruleScriptTestDTO) {
        ScriptResult result = RuleEngineHelper.runScript(ruleScriptTestDTO.getScriptCode(), ruleScriptTestDTO.getTenantId(), ruleScriptTestDTO.getParams());
        return Results.success(result);
    }

    @ApiOperation(value = "创建规则脚本")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<RuleScript> createRuleScript(@RequestBody RuleScript ruleScript) {
        validObject(ruleScript);
        ruleScript.validateRepeat(ruleScriptRepository);
        return Results.success(ruleScriptService.createRuleScript(ruleScript));
    }

    @ApiOperation(value = "修改规则脚本")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<RuleScript> updateRuleScript(@RequestBody @Encrypt RuleScript ruleScript) {
        SecurityTokenHelper.validToken(ruleScript);
        validObject(ruleScript);
        return Results.success(ruleScriptService.updateRuleScript(ruleScript));
    }

    @ApiOperation(value = "删除规则脚本")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity deleteRuleScript(@RequestBody @Encrypt RuleScript ruleScript) {
        SecurityTokenHelper.validToken(ruleScript);
        ruleScriptService.deleteRuleScript(ruleScript.getRuleScriptId());
        return Results.success();
    }
}
