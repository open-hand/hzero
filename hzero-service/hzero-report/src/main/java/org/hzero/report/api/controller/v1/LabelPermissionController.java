package org.hzero.report.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.report.app.service.LabelPermissionService;
import org.hzero.report.config.ReportSwaggerApiConfig;
import org.hzero.report.domain.entity.LabelPermission;
import org.hzero.report.domain.repository.LabelPermissionRepository;
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

/**
 * 标签权限 管理 API
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
@Api(tags = ReportSwaggerApiConfig.LABEL_PERMISSION)
@RestController("labelPermissionController.v1")
@RequestMapping("/v1/{organizationId}/label-permissions")
public class LabelPermissionController extends BaseController {

    @Autowired
    private LabelPermissionRepository labelPermissionRepository;
    @Autowired
    private LabelPermissionService labelPermissionService;

    @ApiOperation(value = "标签权限列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<LabelPermission>> list(@PathVariable Long organizationId, @Encrypt Long labelTemplateId,
                                                      @ApiIgnore @SortDefault(value = LabelPermission.FIELD_PERMISSION_ID,
                                                              direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(labelPermissionRepository.pageLabelPermission(labelTemplateId, organizationId, true, pageRequest));
    }

    @ApiOperation(value = "创建标签权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<LabelPermission> create(@PathVariable Long organizationId, @Encrypt @RequestBody LabelPermission labelPermission) {
        labelPermission.setTenantId(organizationId);
        validObject(labelPermission);
        labelPermissionService.createLabelPermission(labelPermission);
        return Results.success(labelPermission);
    }

    @ApiOperation(value = "修改标签权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<LabelPermission> update(@PathVariable Long organizationId, @Encrypt @RequestBody LabelPermission labelPermission) {
        labelPermission.setTenantId(organizationId);
        validObject(labelPermission);
        SecurityTokenHelper.validToken(labelPermission);
        return Results.success(labelPermissionService.updateLabelPermission(labelPermission));
    }

    @ApiOperation(value = "删除标签权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@Encrypt @RequestBody LabelPermission labelPermission) {
        SecurityTokenHelper.validToken(labelPermission);
        labelPermissionService.removeLabelPermission(labelPermission.getPermissionId());
        return Results.success();
    }

}
