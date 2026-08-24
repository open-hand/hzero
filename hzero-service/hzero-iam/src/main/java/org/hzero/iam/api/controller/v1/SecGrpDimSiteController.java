package org.hzero.iam.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.app.service.SecGrpDclDimService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.repository.SecGrpDclDimRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 安全组数据权限维度 管理 API
 *
 * @author bojiangzhou 2020/02/19 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Api(tags = SwaggerApiConfig.SEC_GRP_DCL_DIM_SITE)
@RestController("secGrpDclDimSiteController.v1")
@RequestMapping("/v1/{secGrpId}")
public class SecGrpDimSiteController extends BaseController {

    @Autowired
    private SecGrpDclDimRepository secGrpDclDimRepository;
    @Autowired
    private SecGrpDclDimService secGrpDclDimService;

    @ApiOperation(value = "平台层-分页查询安全组数据权限维度列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/sec-grp-dcl-dims")
    public ResponseEntity<Page<SecGrpDclDimDTO>> listAssignableDim(
            @ApiParam("安全组ID") @PathVariable(name = "secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpDclDimDTO dclDimDTO,
            @ApiIgnore PageRequest pageRequest) {

        return Results.success(secGrpDclDimRepository.listSecGrpAssignableDim(null, secGrpId, dclDimDTO, pageRequest));
    }

    @ApiOperation(value = "平台层-分页查询安全组数据权限维度列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/sec-grp-dcl-dims/assigned")
    public ResponseEntity<Page<SecGrpDclDimDTO>> listAssignedDim(
            @ApiParam("安全组ID") @PathVariable(name = "secGrpId") @Encrypt Long secGrpId,
            @Encrypt SecGrpDclDimDTO dclDimDTO,
            @ApiIgnore PageRequest pageRequest) {

        return Results.success(secGrpDclDimRepository.listSecGrpAssignedDim(null, secGrpId, dclDimDTO, pageRequest));
    }

    @ApiOperation(value = "平台层-批量新增或者更新安全组数据权限维度列表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/sec-grp-dcl-dims")
    public ResponseEntity<Void> batchSave(
            @ApiParam("安全组ID") @PathVariable(name = "secGrpId") @Encrypt Long secGrpId,
            @RequestBody @Encrypt List<SecGrpDclDimDTO> secGrpDclDimDTOList) {

        secGrpDclDimService.batchSaveSecGrpDim(null, secGrpId, secGrpDclDimDTOList);
        return Results.success();
    }
}
