package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.app.service.DocTypeDimensionAssignService;
import org.hzero.iam.app.service.SecGrpDclService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 安全组数据权限 管理 API
 *
 * @author bojiangzhou 2020/02/12 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_DCL)
@RestController("secGrpDclController.v1")
@RequestMapping("/v1")
public class SecGrpDclController extends BaseController {

    @Autowired
    private SecGrpDclService secGrpDclService;
    @Autowired
    private DocTypeDimensionAssignService docTypeDimensionAssignService;

    @ApiOperation("安全组数据权限——我的安全组查询数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grp-dcls/{secGrpId}/authority")
    public ResponseEntity<SecGrpDclDTO> querySecGrpDclAuthority(
            @ApiParam(value = "租户ID") @PathVariable Long organizationId,
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclService.querySecGrpDclAuthority(secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("安全组数据权限——我的安全组查询可以添加的数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grp-dcls/{secGrpId}/authority/assignable")
    public ResponseEntity<SecGrpDclDTO> querySecGrpDclAssignableAuthority(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclService.querySecGrpDclAssignableAuthority(secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("安全组数据权限-上级安全组或子安全组查询已添加的数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grp-dcls/{secGrpId}/authority/assigned")
    public ResponseEntity<SecGrpDclDTO> querySecGrpDclAssignedAuthority(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @Encrypt SecGrpDclQueryDTO queryDTO,
            @ApiIgnore PageRequest pageRequest) {
        return Results.success(secGrpDclService.querySecGrpDclAssignedAuthority(secGrpId, queryDTO, pageRequest));
    }

    @ApiOperation("安全组数据权限-新增安全组数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/sec-grp-dcls/{secGrpId}/authority")
    public ResponseEntity<Void> saveSecGrpDclAuthority(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @ApiParam(value = "权限类型编码") @RequestParam String authorityTypeCode,
            @ApiParam(value = "安全组数据权限行列表") @RequestBody @Encrypt List<SecGrpDclLine> dclLines) {

        secGrpDclService.saveSecGrpDclAuthority(secGrpId, authorityTypeCode, dclLines);
        return Results.success();
    }

    @ApiOperation("安全组数据权限-删除安全组数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/sec-grp-dcls/{secGrpId}/authority")
    public ResponseEntity<Void> deleteSecGrpDclAuthority(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId,
            @ApiParam(value = "权限类型编码") @RequestParam String authorityTypeCode,
            @ApiParam(value = "安全组数据权限行列表") @RequestBody @Encrypt List<SecGrpDclLine> dclLines) {

        secGrpDclService.deleteSecGrpDclAuthority(secGrpId, authorityTypeCode, dclLines);
        return Results.success();
    }

    @ApiOperation(value = "查询安全组单据类型定义维度分配列表")
    @Permission(permissionLogin = true)
    @GetMapping("/sec-grp-dcls/{secGrpId}/dims/assigned")
    public ResponseEntity<List<DocTypeDimension>> listSecGrpAssign(
            @ApiParam(value = "安全组ID") @PathVariable @Encrypt Long secGrpId) {
        return Results.success(docTypeDimensionAssignService.listSecGrpAssign(secGrpId));
    }

}
