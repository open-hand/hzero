package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.*;
import org.hzero.iam.app.service.DataAuthManagerService;
import org.hzero.iam.app.service.RoleAuthorityService;
import org.hzero.iam.app.service.UserAuthorityService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthDataLine;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jianbo.li
 * @date 2019/10/17 18:20
 */
@Api(tags = SwaggerApiConfig.DATA_AUTH_MANAGER)
@RestController("dataAuthManagerController.v1")
@RequestMapping("/v1/{organizationId}/data-auth-manager")
public class DataAuthManagerController extends BaseController {
    @Autowired
    private RoleAuthDataLineRepository roleAuthDataLineRepository;
    @Autowired
    private DocTypeDimensionRepository docTypeDimensionRepository;
    @Autowired
    private UserAuthorityLineRepository userAuthorityLineRepository;
    @Autowired
    private RoleAuthorityService roleAuthorityService;
    @Autowired
    private RoleAuthDataRepository roleAuthDataRepository;
    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserAuthorityService userAuthorityService;
    @Autowired
    private DataAuthManagerService dataAuthManagerService;

    @ApiOperation("权限数据/tab - 树形查询公司/业务实体/库存组织权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/authority-org")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<ResponseCompanyOuInvorgDTO> treeRoleAuthority(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                        @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                                        @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName) {
        return Results.success(userAuthorityService.listComanyOuInvorgAll(organizationId, dataCode, dataName));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询采购组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/purorgs")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pagePurOrg(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                             @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                             @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                             @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pagePurOrgAll(organizationId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询采购员")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/puragents")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pagePurAgent(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                               @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                               @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                               @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pagePurAgentAll(organizationId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询值集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/lovs")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageLov(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                          @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                          @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                          @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pageLovAll(organizationId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询值集视图")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/lov-views")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageLovView(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                              @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                              @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                              @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pageLovViewAll(organizationId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询数据源")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/datasources")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageDatasource(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                 @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
                                                                 @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
                                                                 @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pageDatasourceAll(organizationId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "权限数据/tab - 分页查询数据组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/data-group")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataLine>> pageDataGroup(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String groupCode,
                                                                @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String groupName,
                                                                @ApiIgnore @SortDefault(value = RoleAuthDataLine.FIELD_DATA_CODE, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleAuthDataLineRepository.pageDataGroupAll(organizationId, groupCode, groupName, pageRequest));
    }

    @ApiOperation(value = "获取数据权限维度列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/dimension")
    @CustomPageRequest
    public ResponseEntity<List<DocTypeDimension>> listDataDimension() {
        return Results.success(docTypeDimensionRepository.selectAll());
    }

    @ApiOperation(value = "数据权限/tab - 用户层分页查询采购品类")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/purcats")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> listPurcat(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(userAuthorityLineRepository.listPurCatAll(organizationId, dataCode, dataName, pageRequest));
    }


    @ApiOperation(value = "权限维护 - 权限维度，权限分配角色列表查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/{docTypeId}/page-assign-role")
    @ProcessLovValue(targetField = {"", RoleAuthorityDTO.FIELD_RULE_LINE})
    public ResponseEntity<Page<RoleAuthorityDTO>> queryAssignRole(@ApiParam(value = "租户id", required = true) @PathVariable Long organizationId,
                                                                  @Encrypt @PathVariable("docTypeId") Long docTypeId,
                                                                  @RequestParam(name = "roleCode", required = false) String roleCode,
                                                                  @ApiIgnore PageRequest pageRequest) {
        return Results.success(roleAuthorityService.pageDocTypeAssignRoles(organizationId,
                docTypeId,
                roleCode,
                pageRequest));
    }

    @ApiOperation(value = "权限维护 - 权限维度，权限分配角色（新建or更新）")
    @Permission(permissionLogin = true)
    @PostMapping("/role/save-all-role")
    public ResponseEntity<List<RoleAuthorityDTO>> batchCreateOrUpdateAllRoleAuthority(@RequestBody List<RoleAuthorityDTO> roleAuthorityDtos) {
        roleAuthorityDtos.forEach(item -> {
            item.getRoleAuthorityLines().forEach(line -> {
                if (null == line.getRoleId()) {
                    line.setRoleId(item.getRoleId());
                }
            });
        });
        roleAuthorityService.batchCreateOrUpdateRoleAuthority(roleAuthorityDtos);
        return Results.success(roleAuthorityDtos);
    }

    @ApiOperation(value = "权限维护 - 权限数据（角色），已分配数据权限角色查询")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/role/data/{dataId}/page-assign-role")
    @CustomPageRequest
    public ResponseEntity<Page<RoleAuthDataDTO>> pageAuthDataAssignedRole(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                          AuthDataDTO authDto,
                                                                          RoleAuthData roleAuthData,
                                                                          @ApiIgnore PageRequest pageRequest) {
        roleAuthData.setTenantId(organizationId);
        roleAuthData.setAuthorityTypeCode(authDto.getAuthorityTypeCode());
        // 限制角色可见范围
        roleAuthData.setUserId(UserUtils.getUserDetails().getUserId());
        roleAuthData.setOrganizationId(UserUtils.getUserDetails().getOrganizationId());
        return Results.success(roleAuthDataRepository.pageRoleAuthDataAssignedRole(authDto.getDataId(), roleAuthData, pageRequest));
    }

    @ApiOperation(value = "权限维护 - 权限数据（角色），新建 or 更新")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/role/data/assign-role")
    @CustomPageRequest
    public ResponseEntity<RoleAuthDataDTO> saveRoleAuthData(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                            @RequestBody RoleAuthDataDTO roleAuthDataDTO) {
        return Results.success(dataAuthManagerService.saveRoleAuthData(organizationId, roleAuthDataDTO));
    }

    @ApiOperation(value = "权限维护 - 权限数据（用户），已分配用户列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/user/data/{dataId}/page-assign-user")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDTO>> pageDataAssignedUser(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                       AuthDataDTO authDto,
                                                                       UserAuthority userAuthority,
                                                                       @ApiIgnore PageRequest pageRequest) {
        userAuthority.setTenantId(organizationId);
        return Results.success(userAuthorityRepository.pageDataAssignedUser(authDto.getDataId(), authDto.getAuthorityTypeCode(), userAuthority, pageRequest));
    }

    @ApiOperation(value = "权限维护 - 权限数据（用户），新建 or 更新 ")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/user/data/assign-user")
    @CustomPageRequest
    public ResponseEntity<List<UserAuthorityDTO>> saveUserAuthData(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
                                                                   @RequestBody @ApiParam(value = "用户数据权限头行", required = true) List<UserAuthorityDTO> userAuthorityDTOList) {

        return Results.success(dataAuthManagerService.saveUserAuthData(organizationId, userAuthorityDTOList));
    }
}
