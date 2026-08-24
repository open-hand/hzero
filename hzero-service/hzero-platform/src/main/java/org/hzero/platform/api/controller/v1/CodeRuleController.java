package org.hzero.platform.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

/**
 * <p>
 * 编码规则 API
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/15 16:58
 */
@Api(tags = PlatformSwaggerApiConfig.CODE_RULE)
@RestController("codeRuleController.v1")
@RequestMapping("/v1/{organizationId}")
public class CodeRuleController extends BaseController {
    private CodeRuleService codeRuleService;
    private CodeRuleDetailRepository codeRuleDetailRepository;
    private CodeRuleDistRepository codeRuleDistRepository;

    @Autowired
    public CodeRuleController(CodeRuleService codeRuleService,
                              CodeRuleDetailRepository codeRuleDetailRepository,
                              CodeRuleDistRepository codeRuleDistRepository) {
        this.codeRuleService = codeRuleService;
        this.codeRuleDetailRepository = codeRuleDetailRepository;
        this.codeRuleDistRepository = codeRuleDistRepository;
    }

    /**
     * 租户级获得生成的编码规则值
     *
     * @param codeRuleDTO 编码规则接收参数dto
     * @return 生成的编码规则值
     */
    @ApiOperation(value = "获得生成的编码规则值", notes = "获得生成的编码规则值")
    @PostMapping("/code-rule/number")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    public ResponseEntity getTenantRuleCode(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                            @RequestBody CodeRuleParamDTO codeRuleDTO) {
        Assert.notNull(codeRuleDTO.getLevelCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(codeRuleDTO.getRuleCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(codeRuleDTO.getLevelValue(), BaseConstants.ErrorCode.DATA_INVALID);
        return Results.success(this.codeRuleService.generateTenantLevelCode(tenantId, codeRuleDTO.getRuleCode(),
                codeRuleDTO.getLevelCode(), codeRuleDTO.getLevelValue(), codeRuleDTO.getVariableMap()));
    }

    @ApiOperation(value = "根据id获取规则明细(组件Feign调用)", notes = "根据id获取规则明细(组件Feign调用)")
    @GetMapping("/code-rule/code-rule-detail/{ruleDetailId}")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<CodeRuleDetail> detail(@PathVariable Long ruleDetailId) {
        return Results.success(codeRuleDetailRepository.selectByPrimaryKey(ruleDetailId));
    }

    @ApiOperation(value = "更新规则明细(组件Feign调用)", notes = "更新规则明细(组件Feign调用)")
    @PutMapping("/code-rule/code-rule-detail")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<Void> updateDetail(@PathVariable("organizationId") Long tenantId, @RequestBody CodeRuleDetail codeRuleDetail) {
        codeRuleService.updateDetail(tenantId, codeRuleDetail);
        return Results.success();
    }

    @ApiOperation(value = "更新规则分配为启用(组件Feign调用)", notes = "更新规则分配为启用(组件Feign调用)")
    @PutMapping("/code-rule/code-rule-dist/{ruleDistId}")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<Void> updateDistUseFlag(@PathVariable Long ruleDistId) {
        codeRuleDistRepository.updateDistUseFlag(ruleDistId);
        return Results.success();
    }

    /**
     * 根据编码获取规则明细
     *
     * @param organizationId     当前用户请求的编码规则的租户ID，因为降级会导致 tenantId 可能转为0,所以单独传
     * @param tenantId           租户ID
     * @param ruleCode           编码
     * @param ruleLevel          层级 P/T
     * @param levelCode          GLOBAL/COM
     * @param levelValue         层级值
     * @param previousRuleLevel  实际请求层级
     * @param previousLevelValue 实际层级值
     * @return List<CodeRuleDetail>
     */
    @ApiOperation("更新规则分配为启用(组件Feign调用)")
    @GetMapping("/code-rule-details/previous")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    public ResponseEntity<List<CodeRuleDetail>> listCodeRuleWithPrevious(@PathVariable Long organizationId,
                                                                         @RequestParam Long tenantId,
                                                                         @RequestParam String ruleCode,
                                                                         @RequestParam String ruleLevel,
                                                                         @RequestParam String levelCode,
                                                                         @RequestParam String levelValue,
                                                                         @RequestParam(required = false) String previousRuleLevel,
                                                                         @RequestParam(required = false) String previousLevelValue) {
        return Results.success(codeRuleService.listCodeRuleWithPrevious(tenantId, ruleCode, ruleLevel, levelCode, levelValue, previousRuleLevel, previousLevelValue));
    }
}
