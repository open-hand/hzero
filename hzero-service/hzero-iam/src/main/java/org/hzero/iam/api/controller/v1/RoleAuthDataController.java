package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.ResponseCompanyOuInvorgDTO;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.app.service.RoleAuthDataLineService;
import org.hzero.iam.app.service.RoleAuthDataService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.RoleAuthDataLine;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 角色单据权限管理 管理 API
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
@Api(tags = SwaggerApiConfig.ROLE_DATA_AUTHORITY)
@RestController("roleAuthDataController.v1")
@RequestMapping("/v1/{organizationId}/role/{roleId}")
public class RoleAuthDataController extends BaseController {

    private RoleAuthDataService roleAuthDataService;
    private RoleAuthDataLineService roleAuthDataLineService;

    @Autowired
    public RoleAuthDataController(RoleAuthDataService roleAuthDataService,
                                  RoleAuthDataLineService roleAuthDataLineService) {
        this.roleAuthDataService = roleAuthDataService;
        this.roleAuthDataLineService = roleAuthDataLineService;
    }

    @ApiOperation("角色单据权限管理列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/authority")
    @CustomPageRequest
    public ResponseEntity<RoleAuthDataDTO> page(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                @ApiParam(value = "类型", required = true) @RequestParam("authorityTypeCode") String authorityTypeCode,
                                                @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataService.pageRoleAuthDataLine(organizationId, roleId, authorityTypeCode, dataCode, dataName, pageRequest));
    }

    @ApiOperation("新增角色单据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority")
    public ResponseEntity<RoleAuthDataDTO> create(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                  @ApiParam(value = "用户数据权限头行", required = true) @RequestBody RoleAuthDataDTO roleAuthData) {
        Assert.notNull(roleAuthData.getRoleAuthData(), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(roleAuthData.getRoleAuthData().setTenantId(organizationId));
        SecurityTokenHelper.validTokenIgnoreInsert(roleAuthData.getRoleAuthData());
        return Results.success(roleAuthDataService.createRoleAuthDataLine(roleAuthData));
    }

    @ApiOperation("删除角色单据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/authority")
    public ResponseEntity<Void> delete(@ApiParam(value = "用户数据权限头行", required = true) @RequestBody List<RoleAuthDataLine> roleAuthDataLineList) {
        SecurityTokenHelper.validToken(roleAuthDataLineList);
        roleAuthDataLineService.deleteRoleAuthDataLine(roleAuthDataLineList);
        return Results.success();
    }

    @ApiOperation("角色树形查询公司/业务实体/库存组织权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/authority-org")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<ResponseCompanyOuInvorgDTO> treeRoleAuthority(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                        @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                                        @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                                        @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName) {
        return Results.success(roleAuthDataService.treeRoleAuthority(organizationId, roleId, dataCode, dataName));
    }

    @ApiOperation("角色树形新增公司/业务实体/库存组织权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority-org")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<List<CompanyOuInvorgDTO>> createRoleAuthority(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                        @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                                        @ApiParam(value = "公司业务实体库存组织", required = true) @Encrypt @RequestBody List<CompanyOuInvorgDTO> companyOuInvorgDTOList) {
        return Results.success(roleAuthDataService.createRoleAuthority(organizationId, roleId, companyOuInvorgDTOList));
    }

    @ApiOperation(value = "角色层分页查询采购组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/purorgs")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pagePurOrg(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                             @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                             @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                             @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                             @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pagePurOrg(organizationId, roleId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "角色层分页查询采购员")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/puragents")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pagePurAgent(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                               @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                               @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                               @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                               @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pagePurAgent(organizationId, roleId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "角色层分页查询值集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/lovs")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageLov(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                          @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                          @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                          @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                          @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pageLov(organizationId, roleId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "角色层分页查询值集视图")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/lov-views")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageLovView(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                              @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                              @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                              @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                              @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pageLovView(organizationId, roleId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "角色层分页查询数据源")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/datasources")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageDatasource(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                 @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                                 @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                                 @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                                 @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pageDatasource(organizationId, roleId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "角色层分页查询数据组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/data-group")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageDataGroup(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
                                                                @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String groupCode,
                                                                @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String groupName,
                                                                @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineService.pageDataGroup(organizationId, roleId, groupCode, groupName, pageRequest));
    }

    @ApiOperation(value = "角色层权限复制")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/copy")
    public ResponseEntity<?> copyUserAuthority(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @Encrypt @ApiParam(value = "角色ID", required = true) @PathVariable("roleId") Long roleId,
            @ApiParam(value = "需要复制Role的ID列表", required = true) @RequestBody @Encrypt List<Long> copyRoleIdList) {
        roleAuthDataService.copyRoleAuthority(organizationId, roleId, copyRoleIdList);
        return Results.success();
    }
}
