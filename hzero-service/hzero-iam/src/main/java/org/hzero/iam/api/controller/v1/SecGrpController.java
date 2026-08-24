package org.hzero.iam.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.api.dto.SecGrpQuickCreateDTO;
import org.hzero.iam.app.service.SecGrpService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * 租户级——安全组管理 API
 *
 * @author bojiangzhou 2020/02/10 代码重构
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Api(tags = SwaggerApiConfig.SEC_GRP)
@RestController("secGrpController.v1")
@RequestMapping("/v1")
public class SecGrpController extends BaseController {

    @Autowired
    private SecGrpService secGrpService;
    @Autowired
    private SecGrpRepository secGrpRepository;

    @ApiOperation(value = "租户层-安全组维护-创建安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/sec-grps/create")
    public ResponseEntity<SecGrp> create(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody @Encrypt SecGrp secGrp) {
        secGrp = secGrpService.createSecGrp(tenantId, secGrp);
        return Results.success(secGrp);
    }

    @ApiOperation(value = "租户层-安全组维护-复制安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/sec-grps/{secGrpId}/copy")
    public ResponseEntity<SecGrp> copyCreate(@PathVariable(name = "organizationId") Long tenantId,
                                             @PathVariable(name = "secGrpId") @Encrypt Long sourceSecGrpId,
                                             @RequestBody @Encrypt SecGrp secGrp) {
        secGrp = secGrpService.copySecGrp(tenantId, sourceSecGrpId, secGrp);
        return Results.success(secGrp);
    }

    @ApiOperation(value = "租户层-安全组维护-快速创建安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/sec-grps/quick-create")
    public ResponseEntity<SecGrp> quickCreate(@PathVariable(name = "organizationId") Long tenantId,
                                              @RequestBody SecGrpQuickCreateDTO dto) {
        SecGrp secGrp = secGrpService.quickCreateSecGrp(tenantId, dto.getSourceSecGrpIds(), dto.getSecGrp(), dto.getRoleId());
        return Results.success(secGrp);
    }

    @ApiOperation(value = "租户层-安全组维护-更新安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/sec-grps")
    public ResponseEntity<SecGrp> update(@PathVariable("organizationId") Long tenantId,
                                         @RequestBody @Encrypt SecGrp secGrp) {
        SecurityTokenHelper.validToken(secGrp);
        validObject(secGrp);
        secGrp = secGrpService.updateSecGrp(tenantId, secGrp);
        return Results.success(secGrp);
    }


    @ApiOperation(value = "租户层-安全组维护-分页查询安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grps")
    public ResponseEntity<Page<SecGrp>> listSecGrp(
            @PathVariable("organizationId") Long tenantId,
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(secGrpRepository.listSecGrp(tenantId, queryDTO, pageRequest));
    }

    @ApiOperation(value = "租户层-安全组维护-查询可快速创建的安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grps/quick-create")
    public ResponseEntity<Page<SecGrp>> listSecGrpForQuickCreate(
            @PathVariable("organizationId") Long tenantId,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest,
            @Encrypt SecGrpQueryDTO secGrp) {
        return Results.success(secGrpRepository.listSecGrpForQuickCreate(tenantId, secGrp, pageRequest));
    }

    @ApiOperation(value = "租户层-安全组维护-查询安全组明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/sec-grps/{secGrpId}")
    public ResponseEntity<SecGrp> query(@PathVariable("organizationId") Long tenantId,
                                        @PathVariable @Encrypt Long secGrpId,
                                        @RequestParam(value = "roleId", required = false) @Encrypt Long roleId) {
        SecGrp secGrp = secGrpRepository.querySecGrp(tenantId, roleId, secGrpId);
        return Results.success(secGrp);
    }


    @ApiOperation(value = "租户层-安全组维护-删除草稿状态的安全组")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/sec-grps")
    public ResponseEntity<Void> deleteDraftSecGrp(@PathVariable("organizationId") Long tenantId,
                                                  @RequestBody @Encrypt SecGrp secGrp) {
        SecurityTokenHelper.validToken(secGrp);
        secGrpService.deleteDraftSecGrp(tenantId, secGrp.getSecGrpId());
        return Results.success();
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "安全组编码重名校验")
    @PostMapping("/sec-grps/check-duplicate")
    public ResponseEntity<Void> checkDuplicate(@RequestParam Long tenantId,
                                               @RequestParam String secGrpCode,
                                               @RequestParam String level) {
        secGrpService.checkDuplicate(tenantId, level, secGrpCode);
        return Results.success();
    }

}
