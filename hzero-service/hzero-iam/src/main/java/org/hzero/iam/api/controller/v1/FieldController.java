package org.hzero.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.app.service.FieldPermissionService;
import org.hzero.iam.app.service.FieldService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Field;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 接口字段维护 管理 API
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@Api(tags = SwaggerApiConfig.FIELD_PERMISSION)
@RestController("fieldController.v1")
@RequestMapping("/v1/{organizationId}/apis")
@SuppressWarnings("unused")
public class FieldController extends BaseController {

    private FieldService fieldService;
    private FieldPermissionService fieldPermissionService;

    @Autowired
    public FieldController(FieldService fieldService, FieldPermissionService fieldPermissionService) {
        this.fieldService = fieldService;
        this.fieldPermissionService = fieldPermissionService;
    }

    @ApiOperation("分页查询接口列表")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<PermissionFieldResponse>> pageApi(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                 @ApiParam("服务") @RequestParam(required = false) String serviceName,
                                                                 @ApiParam("请求方式") @RequestParam(required = false) String method,
                                                                 @ApiParam("请求路径") @RequestParam(required = false) String path,
                                                                 @ApiParam("服务") @RequestParam(required = false) String description,
                                                                 @ApiParam("默认查询的的API是维护了字段的") @RequestParam(required = false, defaultValue = "false") boolean includeAll,
                                                                 @Encrypt @ApiParam("角色ID") @RequestParam(required = false) Long roleId,
                                                                 @Encrypt @ApiParam("用户ID") @RequestParam(required = false) Long userId,
                                                                 @ApiIgnore @SortDefault(value = Permission.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fieldService.pageApi(serviceName, method, path, description, includeAll, roleId, userId, pageRequest));
    }

    // 字段维护

    @ApiOperation("接口字段维护列表")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{permissionId}/fields")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<Field>> pageField(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                 @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                 @ApiParam("字段名称") @RequestParam(required = false) String fieldName,
                                                 @ApiParam("字段类型") @RequestParam(required = false) String fieldType,
                                                 @ApiParam("字段描述") @RequestParam(required = false) String fieldDescription,
                                                 @ApiIgnore @SortDefault(Field.FIELD_ORDER_SEQ) PageRequest pageRequest) {
        return Results.success(fieldService.pageField(permissionId, fieldName, fieldType, fieldDescription, pageRequest));
    }

    @ApiOperation("创建接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/fields")
    public ResponseEntity<Field> createField(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                             @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                             @RequestBody Field field) {
        Assert.isTrue(UniqueHelper.valid(field), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        validObject(field.setPermissionId(permissionId));
        return Results.success(fieldService.createField(field));
    }

    @ApiOperation("批量创建接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/fields/batch")
    public ResponseEntity<List<Field>> createFieldBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                        @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                        @RequestBody List<Field> fieldList) {
        fieldList.forEach(item -> Assert.isTrue(UniqueHelper.valid(item.setPermissionId(permissionId)), BaseConstants.ErrorCode.ERROR_CODE_REPEAT));
        validList(fieldList);
        return Results.success(fieldService.createField(fieldList));
    }

    @ApiOperation("修改接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/fields")
    public ResponseEntity<Field> updateField(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                             @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                             @RequestBody Field field) {
        Assert.isTrue(UniqueHelper.valid(field), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        SecurityTokenHelper.validToken(field.setPermissionId(permissionId));
        validObject(field);
        return Results.success(fieldService.updateField(field));
    }

    @ApiOperation("批量修改接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/fields/batch")
    public ResponseEntity<List<Field>> updateFieldBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                        @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                        @RequestBody List<Field> fieldList) {
        fieldList.forEach(item -> Assert.isTrue(UniqueHelper.valid(item.setPermissionId(permissionId)), BaseConstants.ErrorCode.ERROR_CODE_REPEAT));
        SecurityTokenHelper.validToken(fieldList);
        validList(fieldList);
        return Results.success(fieldService.updateField(fieldList));
    }

    @ApiOperation("删除接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{permissionId}/fields")
    public ResponseEntity<Void> deleteField(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                            @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                            @RequestBody Field field) {
        SecurityTokenHelper.validToken(field);
        fieldService.deleteField(field);
        return Results.success();
    }

    @ApiOperation("批量删除接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{permissionId}/fields/batch")
    public ResponseEntity<Void> deleteFieldBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                 @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                 @RequestBody List<Field> fieldList) {
        SecurityTokenHelper.validToken(fieldList);
        fieldService.deleteField(fieldList);
        return Results.success();
    }

    // 权限维护

    @ApiOperation("角色层查询字段权限列表")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{permissionId}/role/{roleId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<FieldPermission>> pageRolePermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                    @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                    @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId,
                                                                    @ApiParam("字段描述") @RequestParam(required = false) String fieldDescription,
                                                                    @ApiParam("权限类型") @RequestParam(required = false) String permissionType,
                                                                    @ApiIgnore @SortDefault(value = FieldPermission.FIELD_FIELD_PERMISSION_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fieldPermissionService.pagePermission(organizationId, permissionId, Constants.FieldPermissionDimension.ROLE, roleId, fieldDescription, permissionType, pageRequest));
    }

    @ApiOperation("用户层查询字段权限列表")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{permissionId}/user/{userId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<FieldPermission>> pageUserPermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                    @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                    @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId,
                                                                    @ApiParam("字段描述") @RequestParam(required = false) String fieldDescription,
                                                                    @ApiParam("权限类型") @RequestParam(required = false) String permissionType,
                                                                    @ApiIgnore @SortDefault(value = FieldPermission.FIELD_FIELD_PERMISSION_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fieldPermissionService.pagePermission(organizationId, permissionId, Constants.FieldPermissionDimension.USER, userId, fieldDescription, permissionType, pageRequest));
    }

    @ApiOperation("角色层新增字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/role/{roleId}")
    public ResponseEntity<FieldPermission> createRolePermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId,
                                                                @RequestBody FieldPermission fieldPermission) {
        fieldPermission.setTenantId(organizationId);
        fieldPermission.setPermissionDimension(Constants.FieldPermissionDimension.ROLE);
        fieldPermission.setDimensionValue(roleId);
        validObject(fieldPermission);
        return Results.success(fieldPermissionService.createPermission(fieldPermission));
    }

    @ApiOperation("用户层新增字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/user/{userId}")
    public ResponseEntity<FieldPermission> createUserPermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId,
                                                                @RequestBody FieldPermission fieldPermission) {
        fieldPermission.setTenantId(organizationId);
        fieldPermission.setPermissionDimension(Constants.FieldPermissionDimension.USER);
        fieldPermission.setDimensionValue(userId);
        validObject(fieldPermission);
        return Results.success(fieldPermissionService.createPermission(fieldPermission));
    }

    @ApiOperation("角色层批量新增字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/role/{roleId}/batch")
    public ResponseEntity<List<FieldPermission>> createRolePermissionBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                           @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                           @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId,
                                                                           @RequestBody List<FieldPermission> fieldPermissionList) {
        fieldPermissionList.forEach(item -> item.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.ROLE)
                .setDimensionValue(roleId));
        validList(fieldPermissionList);
        return Results.success(fieldPermissionService.createPermission(fieldPermissionList));
    }

    @ApiOperation("用户层批量新增字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{permissionId}/user/{userId}/batch")
    public ResponseEntity<List<FieldPermission>> createUserPermissionBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                           @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                           @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId,
                                                                           @RequestBody List<FieldPermission> fieldPermissionList) {
        fieldPermissionList.forEach(item -> item.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.USER)
                .setDimensionValue(userId));
        validObject(fieldPermissionList);
        return Results.success(fieldPermissionService.createPermission(fieldPermissionList));
    }

    @ApiOperation("角色层更新字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/role/{roleId}")
    public ResponseEntity<FieldPermission> updateRolePermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId,
                                                                @RequestBody FieldPermission fieldPermission) {
        SecurityTokenHelper.validToken(fieldPermission.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.ROLE)
                .setDimensionValue(roleId));
        validObject(fieldPermission);
        return Results.success(fieldPermissionService.updatePermission(fieldPermission));
    }

    @ApiOperation("用户层更新字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/user/{userId}")
    public ResponseEntity<FieldPermission> updateUserPermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId,
                                                                @RequestBody FieldPermission fieldPermission) {
        SecurityTokenHelper.validToken(fieldPermission.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.USER)
                .setDimensionValue(userId));
        validObject(fieldPermission);
        return Results.success(fieldPermissionService.updatePermission(fieldPermission));
    }

    @ApiOperation("角色层批量更新字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/role/{roleId}/batch")
    public ResponseEntity<List<FieldPermission>> updateRolePermissionBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                           @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                           @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable long roleId,
                                                                           @RequestBody List<FieldPermission> fieldPermissionList) {
        fieldPermissionList.forEach(item -> item.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.ROLE)
                .setDimensionValue(roleId));
        SecurityTokenHelper.validToken(fieldPermissionList);
        validList(fieldPermissionList);
        return Results.success(fieldPermissionService.updatePermission(fieldPermissionList));
    }

    @ApiOperation("用户层批量更新字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{permissionId}/user/{userId}/batch")
    public ResponseEntity<List<FieldPermission>> updateUserPermissionBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                                           @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                                           @Encrypt @ApiParam(value = "用户ID", required = true) @PathVariable long userId,
                                                                           @RequestBody List<FieldPermission> fieldPermissionList) {
        fieldPermissionList.forEach(item -> item.setTenantId(organizationId)
                .setPermissionDimension(Constants.FieldPermissionDimension.USER)
                .setDimensionValue(userId));
        SecurityTokenHelper.validToken(fieldPermissionList);
        validObject(fieldPermissionList);
        return Results.success(fieldPermissionService.updatePermission(fieldPermissionList));
    }

    @ApiOperation("删除字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{permissionId}/permission")
    public ResponseEntity<Void> deletePermission(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                 @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                 @RequestBody FieldPermission fieldPermission) {
        SecurityTokenHelper.validToken(fieldPermission);
        validObject(fieldPermission);
        fieldPermissionService.deletePermission(fieldPermission);
        return Results.success();
    }

    @ApiOperation("批量删除字段权限")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{permissionId}/permission/batch")
    public ResponseEntity<Void> deletePermissionBatch(@ApiParam(value = "租户ID", required = true) @PathVariable long organizationId,
                                                      @Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                      @RequestBody List<FieldPermission> fieldPermissionList) {
        SecurityTokenHelper.validToken(fieldPermissionList);
        validObject(fieldPermissionList);
        fieldPermissionService.deletePermission(fieldPermissionList);
        return Results.success();
    }
}
