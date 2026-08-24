package org.hzero.platform.api.controller.v1;

import java.util.Map;

import org.hzero.boot.platform.code.builder.CodeRuleBuilder;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.CodeRuleParamDTO;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.repository.CodeRuleDetailRepository;
import org.hzero.platform.domain.repository.CodeRuleDistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 编码规则 API
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/15 16:58
 */
@Api(tags = PlatformSwaggerApiConfig.CODE_RULE_SITE)
@RestController("codeRuleSiteController.v1")
@RequestMapping("/v1")
public class CodeRuleSiteController extends BaseController {
    private CodeRuleService codeRuleService;
    private CodeRuleDetailRepository codeRuleDetailRepository;
    private CodeRuleDistRepository codeRuleDistRepository;
    private CodeRuleBuilder codeRuleBuilder;

    @Autowired
    public CodeRuleSiteController(CodeRuleService codeRuleService,
                                  CodeRuleDetailRepository codeRuleDetailRepository,
                                  CodeRuleDistRepository codeRuleDistRepository,
                                  CodeRuleBuilder codeRuleBuilder) {
        this.codeRuleService = codeRuleService;
        this.codeRuleDetailRepository = codeRuleDetailRepository;
        this.codeRuleDistRepository = codeRuleDistRepository;
        this.codeRuleBuilder = codeRuleBuilder;
    }

    @ApiOperation("生成编码")
    @PostMapping("/code-rule/generate")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<String> generate(@RequestParam String level,
                                           @RequestParam(required = false) Long tenantId,
                                           @RequestParam String ruleCode,
                                           @RequestParam(required = false, defaultValue = CodeConstants.CodeRuleLevelCode.GLOBAL) String levelCode,
                                           @RequestParam(required = false, defaultValue = CodeConstants.CodeRuleLevelCode.GLOBAL) String levelValue,
                                           @RequestBody Map<String, String> variableMap) {
        return Results.success(codeRuleBuilder.generateCode(level, tenantId, ruleCode, levelCode, levelValue, variableMap));
    }

    /**
     * 获得生成的编码规则值
     *
     * @param codeRuleDTO 编码规则dto
     * @return 生成的编码规则值
     */
    @ApiOperation(value = "获得生成的编码规则值", notes = "获得生成的编码规则值")
    @PostMapping("/code-rule/number")
    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
    public ResponseEntity<String> getRuleCode(@RequestBody CodeRuleParamDTO codeRuleDTO) {
        Assert.notNull(codeRuleDTO.getRuleCode(), BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(this.codeRuleService.generatePlatformLevelCode(codeRuleDTO));
    }

    @ApiOperation(value = "根据id获取规则明细(组件Feign调用)", notes = "根据id获取规则明细(组件Feign调用)")
    @GetMapping("/code-rule/code-rule-detail/{ruleDetailId}")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<CodeRuleDetail> detail(@PathVariable Long ruleDetailId) {
        return Results.success(codeRuleDetailRepository.selectByPrimaryKey(ruleDetailId));
    }

    @ApiOperation(value = "更新规则明细(组件Feign调用)", notes = "更新规则明细(组件Feign调用)")
    @PutMapping("/code-rule/code-rule-detail/")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<Integer> updateDetail(@RequestBody CodeRuleDetail codeRuleDetail) {
        return Results.success(codeRuleDetailRepository.updateCodeRuleDetailWithNotOVN(codeRuleDetail));
    }

    @ApiOperation(value = "更新规则分配为启用(组件Feign调用)", notes = "更新规则分配为启用(组件Feign调用)")
    @PutMapping("/code-rule/code-rule-dist/{ruleDistId}")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<Integer> updateDistUseFlag(@PathVariable Long ruleDistId) {
        return Results.success(codeRuleDistRepository.updateDistUseFlag(ruleDistId));
    }
}
