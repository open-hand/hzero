package org.hzero.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
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
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.api.dto.UserAuthorityDataDTO;
import org.hzero.iam.app.service.UserAuthorityService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.UserAuthorityLine;
import org.hzero.iam.domain.repository.UserAuthorityLineRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 用户权限管理 管理 API
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
@Api(tags = SwaggerApiConfig.USER_AUTHORITY)
@RestController("userAuthorityController.v1")
@RequestMapping("/v1/{organizationId}/users/{userId}")
public class UserAuthorityController extends BaseController {

    @Autowired
    private UserAuthorityLineRepository userAuthorityLineRepository;

    @Autowired
    private UserAuthorityService userAuthorityService;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @ApiOperation(value = "用户层分页查询用户数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/authority")
    @CustomPageRequest
    public ResponseEntity<UserAuthorityDTO> list(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "类型", required = true) @RequestParam("authorityTypeCode") String authorityTypeCode,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityLine.FIELD_AUTHORITY_LINE_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(userAuthorityService.selectCreateUserAuthority(organizationId, userId, authorityTypeCode, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层批量保存用户数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority")
    public ResponseEntity<?> create(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "类型", required = true) @RequestParam("authorityTypeCode") String authorityTypeCode,
            @ApiParam(value = "用户数据权限头行", required = true) @RequestBody @Encrypt UserAuthorityDTO userAuthorityDTO) {

        return Results.success(userAuthorityService.batchCreateUserAuthority(organizationId, userId, authorityTypeCode, userAuthorityDTO));
    }

    @ApiOperation(value = "用户层批量保存用户数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority/batch-save")
    public ResponseEntity<?> batchCreate(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "类型", required = true) @RequestParam("authorityTypeCode") String authorityTypeCode,
            @ApiParam(value = "用户数据权限头行", required = true) @RequestBody @Encrypt List<UserAuthorityDTO> userAuthorityDTOList) {

        if (CollectionUtils.isNotEmpty(userAuthorityDTOList)) {
            userAuthorityDTOList.forEach(userAuthorityDTO -> {
                userAuthorityDTO.getUserAuthority().setTenantId(organizationId);
            });
        }
        return Results.success(userAuthorityService.batchCreateUserAuthority(userAuthorityDTOList));
    }

    @ApiOperation(value = "用户层批量删除用户数据权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/authority")
    public ResponseEntity<?> batchDeleteUserAuthorityLines(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "用户数据权限行数据", required = true) @RequestBody @Encrypt List<UserAuthorityLine> userAuthorityLineList) {
        userAuthorityService.batchDeleteUserAuthorityLines(organizationId, userId, userAuthorityLineList);
        return Results.success();
    }

    @ApiOperation(value = "用户层权限复制")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority/copy")
    public ResponseEntity<?> copyUserAuthority(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "需要复制User的ID列表", required = true) @RequestBody @Encrypt List<Long> copyUserIdList) {
        userAuthorityService.copyUserAuthority(organizationId, userId, copyUserIdList);
        return Results.success();
    }

    @ApiOperation(value = "用户层权限交换")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority/exchange")
    public ResponseEntity<?> exchangeUserAuthority(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "需要交换的用户ID", required = true) @RequestParam @Encrypt Long exchanageUserId) {

        return Results.success(userAuthorityService.exchangeUserAuthority(organizationId, userId, exchanageUserId));
    }


    @ApiOperation(value = "用户层树形查询公司/业务实体/库存组织权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/authority-org")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<?> treeUserAuthority(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName) {
        return Results.success(userAuthorityService.listComanyOuInvorg(organizationId, userId, dataCode, dataName));
    }

    @ApiOperation(value = "用户层树形新增公司/业务实体/库存组织权限")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/authority-org")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<?> createTreeUserAuthority(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "公司业务实体库存组织", required = true) @RequestBody @Encrypt List<CompanyOuInvorgDTO> companyOuInvorgDTOList) {
        return Results.success(userAuthorityService.createTreeUserAuthority(organizationId, userId, companyOuInvorgDTOList));
    }

    @ApiOperation(value = "用户层分页查询采购组织")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/purorgs")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> listPurOrg(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(userAuthorityLineRepository.listPurOrg(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询采购员")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/puragents")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> listPurAgent(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(userAuthorityLineRepository.listPurAgent(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询采购品类")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/purcats")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> listPurcat(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID,
                    direction = Sort.Direction.DESC) PageRequest pageRequest) {

        return Results.success(userAuthorityLineRepository.listPurCat(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询值集")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/lovs")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> pageLov(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(userAuthorityLineRepository.pageLov(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询值集视图")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/lov-views")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> pageLovView(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(userAuthorityLineRepository.pageLovView(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询数据源")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/datasources")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> pageDatasource(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "编码") @RequestParam(value = "dataCode", required = false) String dataCode,
            @ApiParam(value = "名称") @RequestParam(value = "dataName", required = false) String dataName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(userAuthorityLineRepository.pageDatasource(organizationId, userId, dataCode, dataName, pageRequest));
    }

    @ApiOperation(value = "用户层分页查询数据组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/data/data-group")
    @CustomPageRequest
    public ResponseEntity<Page<UserAuthorityDataDTO>> pageDataGroup(
            @ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long organizationId,
            @ApiParam(value = "用户ID", required = true) @PathVariable("userId") @Encrypt Long userId,
            @ApiParam(value = "数据组编码") @RequestParam(value = "dataCode", required = false) String groupCode,
            @ApiParam(value = "数据组名称") @RequestParam(value = "dataName", required = false) String groupName,
            @ApiIgnore @SortDefault(value = UserAuthorityDataDTO.FIELD_DATA_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(userAuthorityLineRepository.pageGroupData(organizationId, userId, groupCode, groupName, pageRequest));
    }
}
