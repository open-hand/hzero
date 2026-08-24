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
import org.hzero.iam.app.service.FieldService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Field;
import org.hzero.iam.domain.entity.Permission;
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
@Api(tags = SwaggerApiConfig.FIELD_PERMISSION_SITE)
@RestController("fieldSiteController.v1")
@RequestMapping("/v1/apis")
@SuppressWarnings("unused")
public class FieldSiteController extends BaseController {

    private FieldService fieldService;

    @Autowired
    public FieldSiteController(FieldService fieldService) {
        this.fieldService = fieldService;
    }

    @ApiOperation("分页查询接口列表")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<PermissionFieldResponse>> pageApi(@ApiParam("服务") @RequestParam(required = false) String serviceName,
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
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @GetMapping("/{permissionId}/fields")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    public ResponseEntity<Page<Field>> pageField(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                 @ApiParam("字段名称") @RequestParam(required = false) String fieldName,
                                                 @ApiParam("字段类型") @RequestParam(required = false) String fieldType,
                                                 @ApiParam("字段描述") @RequestParam(required = false) String fieldDescription,
                                                 @ApiIgnore @SortDefault(Field.FIELD_ORDER_SEQ) PageRequest pageRequest) {
        return Results.success(fieldService.pageField(permissionId, fieldName, fieldType, fieldDescription, pageRequest));
    }

    @ApiOperation("创建接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @PostMapping("/{permissionId}/fields")
    public ResponseEntity<Field> createField(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                             @RequestBody Field field) {
        Assert.isTrue(UniqueHelper.valid(field), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        validObject(field.setPermissionId(permissionId));
        return Results.success(fieldService.createField(field));
    }

    @ApiOperation("批量创建接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @PostMapping("/{permissionId}/fields/batch")
    public ResponseEntity<List<Field>> createFieldBatch(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                        @RequestBody List<Field> fieldList) {
        fieldList.forEach(item -> Assert.isTrue(UniqueHelper.valid(item.setPermissionId(permissionId)), BaseConstants.ErrorCode.ERROR_CODE_REPEAT));
        validList(fieldList);
        return Results.success(fieldService.createField(fieldList));
    }

    @ApiOperation("修改接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @PutMapping("/{permissionId}/fields")
    public ResponseEntity<Field> updateField(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                             @RequestBody Field field) {
        Assert.isTrue(UniqueHelper.valid(field), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        SecurityTokenHelper.validToken(field.setPermissionId(permissionId));
        validObject(field);
        return Results.success(fieldService.updateField(field));
    }

    @ApiOperation("批量修改接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @PutMapping("/{permissionId}/fields/batch")
    public ResponseEntity<List<Field>> updateFieldBatch(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                        @RequestBody List<Field> fieldList) {
        fieldList.forEach(item -> Assert.isTrue(UniqueHelper.valid(item.setPermissionId(permissionId)), BaseConstants.ErrorCode.ERROR_CODE_REPEAT));
        SecurityTokenHelper.validToken(fieldList);
        validList(fieldList);
        return Results.success(fieldService.updateField(fieldList));
    }

    @ApiOperation("删除接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{permissionId}/fields")
    public ResponseEntity<Void> deleteField(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                            @RequestBody Field field) {
        SecurityTokenHelper.validToken(field);
        fieldService.deleteField(field);
        return Results.success();
    }

    @ApiOperation("批量删除接口字段维护")
    @io.choerodon.swagger.annotation.Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/{permissionId}/fields/batch")
    public ResponseEntity<Void> deleteFieldBatch(@Encrypt @ApiParam(value = "接口ID", required = true) @PathVariable long permissionId,
                                                 @RequestBody List<Field> fieldList) {
        SecurityTokenHelper.validToken(fieldList);
        fieldService.deleteField(fieldList);
        return Results.success();
    }
}
