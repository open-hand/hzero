package org.hzero.platform.api.controller.v1;

import java.util.Collection;
import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Pair;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.PermissionRuleDTO;
import org.hzero.platform.app.service.PermissionRuleService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.PermissionRange;
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
@Api(tags = PlatformSwaggerApiConfig.PERMISSION_RULE)
@RestController("permissionRuleController.v1")
@RequestMapping("/v1/{organizationId}/permission-rules")
public class PermissionRuleController extends BaseController {

    @Autowired
    private PermissionRuleRepository permissionRuleRepository;
    @Autowired
    private PermissionRuleService permissionRuleService;

    @ApiOperation(value = "屏蔽规则列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity list(@PathVariable Long organizationId, PermissionRule permissionRule,
                               @ApiIgnore @SortDefault(value = PermissionRule.FIELD_RULE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        permissionRule.setTenantId(organizationId);
        return Results.success(permissionRuleRepository.selectOrgPermissionRule(pageRequest, permissionRule));
    }

    @ApiOperation(value = "创建屏蔽规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @PostMapping
    public ResponseEntity create(@RequestBody PermissionRule permissionRule, @PathVariable Long organizationId) {
        permissionRule.setTenantId(organizationId);
        validObject(permissionRule);
        permissionRuleRepository.insertPermissionRule(permissionRule);
        return Results.success(permissionRule);
    }

    @ApiOperation(value = "修改屏蔽规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true)})
    @PutMapping
    public ResponseEntity update(@RequestBody @Encrypt PermissionRule permissionRule, @PathVariable Long organizationId) {
        permissionRule.setTenantId(organizationId);
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRule);
        validObject(permissionRule);
        permissionRule.checkDataLegalization(permissionRuleRepository);
        permissionRuleRepository.updatePermissionRule(permissionRule);
        return Results.success(permissionRule);
    }

    @ApiOperation(value = "删除屏蔽规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiImplicitParams({@ApiImplicitParam(name = "organizationId", value = "租户ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "ruleId", value = "规则ID", paramType = "path", required = true)})
    @DeleteMapping
    public ResponseEntity remove(@PathVariable Long organizationId, @RequestBody @Encrypt PermissionRule permissionRule) {
        SecurityTokenHelper.validTokenIgnoreInsert(permissionRule);
        permissionRuleRepository.deletePermissionRule(permissionRule.getRuleId(), organizationId);
        return Results.success();
    }

    @ApiOperation("查询可以应用的屏蔽规则")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/rules")
    public ResponseEntity<Page<PermissionRuleDTO>> listRuleEnabled(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                   @RequestParam(required = false) @ApiParam("屏蔽规则编码") String ruleCode,
                                                                   @RequestParam(required = false) @ApiParam("屏蔽规则名称") String ruleName,
                                                                   @ApiIgnore @SortDefault(value = PermissionRule.FIELD_RULE_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(permissionRuleRepository.listRuleEnabled(organizationId, ruleCode, ruleName, pageRequest));
    }

    @ApiOperation("批量保存屏蔽规则和范围")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @PostMapping("/permission-ranges/save")
    public ResponseEntity<List<Pair<PermissionRange, PermissionRule>>> save(@PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
                                                                            @RequestBody List<Pair<PermissionRange, PermissionRule>> permission) {
        validList(permission);
        return Results.success(permissionRuleService.savePermission(organizationId, permission));
    }

    @ApiOperation("批量禁用数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @DeleteMapping("/permission-ranges/disable")
    public void disablePermission(@PathVariable("organizationId") Long tenantId,
                                  @RequestBody List<Long> disablePermissionRuleList) {
        permissionRuleService.disablePermission(tenantId, disablePermissionRuleList);
    }

    @ApiOperation("批量删除数据权限和范围关联")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @DeleteMapping("/permission-ranges/rel/disable")
    public void disableRel(@PathVariable("organizationId") Long tenantId,
                           @RequestBody Collection<Pair<Long, Long>> disableRelList) {
        permissionRuleService.disableRel(tenantId, disableRelList);
    }
}
