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
@Api(tags = PlatformSwaggerApiConfig.RULE_SCRIPT_CONFIG)
@RestController("ruleScriptController.v1")
@RequestMapping("/v1/{organizationId}/rule-scripts")
public class RuleScriptController extends BaseController {

    @Autowired
    private RuleScriptRepository ruleScriptRepository;

    @Autowired
    private RuleScriptService ruleScriptService;

    @ApiOperation(value = "规则脚本列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<RuleScript>> pageRuleScript(RuleScript ruleScript, @PathVariable Long organizationId,
                                                           @ApiIgnore @SortDefault(value = RuleScript.FIELD_RULE_SCRIPT_ID, direction = Sort.Direction.ASC) PageRequest pageRequest) {
        ruleScript.setTenantId(organizationId);
        Page<RuleScript> list = ruleScriptRepository.pageRuleScript(pageRequest, ruleScript);
        return Results.success(list);
    }

    @ApiOperation(value = "规则脚本明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{ruleScriptId}")
    public ResponseEntity<RuleScript> detailRuleScript(@PathVariable Long organizationId, @PathVariable @ApiParam(value = "规则脚本ID", required = true) @Encrypt Long ruleScriptId) {
        RuleScript ruleScript = ruleScriptRepository.selectRuleScript(ruleScriptId, organizationId);
        return Results.success(ruleScript);
    }

    @ApiOperation(value = "测试规则脚本运行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/test")
    public ResponseEntity<ScriptResult> runScript(@RequestBody RuleScriptTestDTO ruleScriptTestDTO, @PathVariable Long organizationId) {
        ScriptResult result = RuleEngineHelper.runScript(ruleScriptTestDTO.getScriptCode(), organizationId, ruleScriptTestDTO.getParams());
        return Results.success(result);
    }

    @ApiOperation(value = "根据编码和租户查询规则")
    @Permission(permissionWithin = true)
    @GetMapping("/code")
    public ResponseEntity<RuleScript> selectRuleScriptByCode(String scriptCode, @PathVariable Long organizationId) {
        RuleScript ruleScript = ruleScriptService.selectRuleScriptByCode(scriptCode, organizationId);
        return Results.success(ruleScript);
    }

    @ApiOperation(value = "创建规则脚本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<RuleScript> createRuleScript(@PathVariable Long organizationId, @RequestBody RuleScript ruleScript) {
        ruleScript.setTenantId(organizationId);
        validObject(ruleScript);
        ruleScript.validateRepeat(ruleScriptRepository);
        return Results.success(ruleScriptService.createRuleScript(ruleScript));
    }

    @ApiOperation(value = "修改规则脚本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<RuleScript> updateRuleScript(@PathVariable Long organizationId, @RequestBody @Encrypt RuleScript ruleScript) {
        SecurityTokenHelper.validToken(ruleScript);
        ruleScript.setTenantId(organizationId);
        validObject(ruleScript);
        ruleScript.checkDataLegalization(ruleScriptRepository);
        return Results.success(ruleScriptService.updateRuleScript(ruleScript));
    }

    @ApiOperation(value = "删除规则脚本")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity deleteRuleScript(@RequestBody @Encrypt RuleScript ruleScript) {
        SecurityTokenHelper.validToken(ruleScript);
        ruleScriptService.deleteRuleScript(ruleScript.getRuleScriptId());
        return Results.success();
    }
}
