package org.hzero.iam.api.controller.v1;

import javax.servlet.http.HttpServletRequest;

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
 * 安全组 管理 API
 *
 * @author bojiangzhou 2020/02/19 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_SITE)
@RestController("secGrpSiteController.v1")
@RequestMapping("/v1/sec-grps")
public class SecGrpSiteController extends BaseController {

    @Autowired
    private SecGrpService secGrpService;
    @Autowired
    private SecGrpRepository secGrpRepository;

    @ApiOperation(value = "平台层安全组维护-创建安全组")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/create")
    public ResponseEntity<SecGrp> create(@RequestBody SecGrp secGrp) {
        return Results.success(secGrpService.createSecGrp(secGrp.getTenantId(), secGrp));
    }

    @ApiOperation(value = "平台层安全组维护-复制安全组")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/{secGrpId}/copy")
    public ResponseEntity<SecGrp> copySecGrp(
            @PathVariable(name = "secGrpId") @Encrypt Long secGrpId,
            @RequestBody SecGrp secGrp) {
        return Results.success(secGrpService.copySecGrp(secGrp.getTenantId(), secGrpId, secGrp));
    }

    @ApiOperation(value = "平台层安全组维护-快速创建安全组")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/quick-create")
    public ResponseEntity<SecGrp> quickCreate(@RequestBody SecGrpQuickCreateDTO dto, HttpServletRequest request) {
        return Results.success(secGrpService.quickCreateSecGrp(null, dto.getSourceSecGrpIds(), dto.getSecGrp(), dto.getRoleId()));
    }

    @ApiOperation(value = "平台层安全组维护-更新安全组")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<SecGrp> update(@RequestBody SecGrp secGrp) {
        SecurityTokenHelper.validToken(secGrp);
        validObject(secGrp);
        return Results.success(secGrpService.updateSecGrp(secGrp.getTenantId(), secGrp));
    }

    @ApiOperation(value = "平台层安全组维护-分页查询安全组")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<SecGrp>> listSecGrp(
            @Encrypt SecGrpQueryDTO queryDTO,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(secGrpRepository.listSecGrp(null, queryDTO, pageRequest));
    }

    @ApiOperation(value = "平台层安全组维护-查询可快速创建的安全组")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/quick-create")
    public ResponseEntity<Page<SecGrp>> listSecGrpForQuickCreate(
            @Encrypt SecGrpQueryDTO secGrp,
            @ApiIgnore @SortDefault(value = SecGrp.FIELD_SEC_GRP_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(secGrpRepository.listSecGrpForQuickCreate(secGrp.getTenantId(), secGrp, pageRequest));
    }

    @ApiOperation(value = "平台层安全组维护-删除草稿状态的安全组")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> deleteDraftSecGrp(@RequestBody SecGrp secGrp) {
        SecurityTokenHelper.validToken(secGrp);
        secGrpService.deleteDraftSecGrp(secGrp.getTenantId(), secGrp.getSecGrpId());
        return Results.success();
    }


    @ApiOperation(value = "平台层安全组维护-查询安全组明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{secGrpId}")
    public ResponseEntity<SecGrp> query(
            @PathVariable @Encrypt Long secGrpId,
            @RequestParam(value = "roleId", required = false) @Encrypt Long roleId) {
        return Results.success(secGrpRepository.querySecGrp(null, roleId, secGrpId));
    }

}
