package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.SecGrpAclApiDTO;
import org.hzero.iam.api.dto.SecGrpAclFieldDTO;
import org.hzero.iam.app.service.SecGrpAclFieldService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.repository.SecGrpAclFieldRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 安全组字段权限 管理 API
 *
 * @author bojiangzhou 2020/02/19 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_ACL_FIELD_SITE)
@RestController("secGrpAclFieldSiteController.v1")
@RequestMapping("/v1/sec-grp-acl-fields/{secGrpId}")
public class SecGrpFieldSiteController extends BaseController {

    @Autowired
    private SecGrpAclFieldRepository fieldRepository;
    @Autowired
    private SecGrpAclFieldService secGrpAclFieldService;


    @ApiOperation("平台层-安全组字段权限维护-分页查询接口列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listAssignableApis(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpAclApiDTO queryDTO,
            @ApiIgnore @SortDefault(value = org.hzero.iam.domain.entity.Permission.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(fieldRepository.listAssignableSecGrpApi(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("租户层-安全组字段权限维护-分页查询接口列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/assigned")
    public ResponseEntity<Page<org.hzero.iam.domain.entity.Permission>> listAssignedApis(
            @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpAclApiDTO queryDTO,
            @ApiIgnore @SortDefault(value = org.hzero.iam.domain.entity.Permission.FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fieldRepository.listAssignedSecGrpApi(null, secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("平台层-安全组字段权限维护-分页查询接口字段列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{permissionId}/fields")
    public ResponseEntity<Page<SecGrpAclField>> listField(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @ApiParam(value = "接口ID") @PathVariable("permissionId") @Encrypt Long permissionId,
            @Encrypt SecGrpAclFieldDTO queryDTO,
            @ApiIgnore @SortDefault(value = SecGrpAclField.FIELD_SEC_GRP_ACL_FIELD_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(fieldRepository.listSecGrpApiField(BaseConstants.DEFAULT_TENANT_ID, secGrpId, permissionId, queryDTO, pageRequest));
    }

    @ApiOperation("平台层-安全组字段权限维护-接口字段新增删除修改")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/operate")
    public ResponseEntity<Void> batchSave(
            @ApiParam(value = "安全组ID") @PathVariable("secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<SecGrpAclField> secGrpAclFields) {

        secGrpAclFieldService.batchSaveSecGrpField(BaseConstants.DEFAULT_TENANT_ID, secGrpId, secGrpAclFields);
        return Results.success();
    }
}
