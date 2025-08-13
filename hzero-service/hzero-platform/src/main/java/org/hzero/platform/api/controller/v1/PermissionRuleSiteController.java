package org.hzero.platform.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.PermissionRuleDTO;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.PermissionRule;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
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
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;

/**
 * 屏蔽规则 管理 API
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@Api(tags = PlatformSwaggerApiConfig.PERMISSION_RULE_SITE)
@RestController("permissionRuleSiteController.v1")
@RequestMapping("/v1/permission-rules")
public class PermissionRuleSiteController extends BaseController {

    @Autowired
    private PermissionRuleRepository permissionRuleRepository;

    @ApiOperation(value = "屏蔽规则列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity list(PermissionRule permissionRule,
                               @ApiIgnore @SortDefault(value = PermissionRule.FIELD_RULE_ID,
                                       direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(permissionRuleRepository.selectPermissionRule(pageRequest, permissionRule));
    }

    @ApiOperation(value = "创建屏蔽规则")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity create(@RequestBody PermissionRule permissionRule) {
        validObject(permissionRule);
        permissionRuleRepository.insertPermissionRule(permissionRule);
        return Results.success(permissionRule);
    }

    @ApiOperation(value = "修改屏蔽规则")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity update(@RequestBody @Encrypt PermissionRule permissionRule) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRule);
        permissionRuleRepository.updatePermissionRule(permissionRule);
        return Results.success(permissionRule);
    }

    @ApiOperation(value = "删除屏蔽规则")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    @ApiImplicitParams({@ApiImplicitParam(name = "ruleId", value = "规则ID", paramType = "path", required = true)})
    public ResponseEntity remove(@RequestBody @Encrypt PermissionRule permissionRule) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRule);
        permissionRuleRepository.deletePermissionRule(permissionRule.getRuleId(), permissionRule.getTenantId());
        return Results.success();
    }

    @ApiOperation("查询可以应用的屏蔽规则")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/rules")
    public ResponseEntity<Page<PermissionRuleDTO>> listRuleEnabled(@RequestParam @ApiParam(value = "租户ID", required = true) Long tenantId,
                                                                   @RequestParam(required = false) @ApiParam("屏蔽规则编码") String ruleCode,
                                                                   @RequestParam(required = false) @ApiParam("屏蔽规则名称") String ruleName,
                                                                   @ApiIgnore @SortDefault(value = PermissionRule.FIELD_RULE_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(permissionRuleRepository.listRuleEnabled(tenantId, ruleCode, ruleName, pageRequest));
    }
}
