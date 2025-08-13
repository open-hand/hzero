package org.hzero.platform.api.controller.v1;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.CertificateQueryDTO;
import org.hzero.platform.app.service.CertificateService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Certificate;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * CA证书配置 管理 API
 *
 * @author xingxing.wu@hand-china.com 2019-09-29 10:38:09
 */
@Api(tags = PlatformSwaggerApiConfig.CERTIFICATE)
@RestController("certificateController.v1")
@RequestMapping("/v1/{organizationId}/certificates")
public class CertificateController extends BaseController {

    @Autowired
    private CertificateService certificateService;

    @ApiOperation(value = "CA证书配置列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<Certificate>> listCertificate(@PathVariable(name = "organizationId") Long organizationId,  @Encrypt CertificateQueryDTO certificate, @ApiIgnore @SortDefault(value = Certificate.FIELD_CERTIFICATE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        certificate.setTenantId(organizationId);
        Page<Certificate> list = certificateService.pageAndSortCertificate(certificate, pageRequest);
        return Results.success(list);
    }

    @ApiOperation(value = "创建CA证书配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Certificate> createCertificate(@PathVariable(name = "organizationId") Long organizationId,
                                                         @RequestParam(name = "customMenuFile") MultipartFile customMenuFile) {

        Certificate certificate = certificateService.createCertificate(organizationId, customMenuFile);
        return Results.success(certificate);
    }

    @ApiOperation(value = "修改CA证书配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/update")
    public ResponseEntity<Certificate> updateCertificate(@RequestParam(name = "certificateId") @Encrypt Long certificateId,
                                                         @PathVariable(name = "organizationId") Long organizationId,
                                                         @RequestParam(name = "customMenuFile") MultipartFile customMenuFile) {
        return Results.success(certificateService.updateCertificate(certificateId, customMenuFile));
    }

    @ApiOperation(value = "删除CA证书配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity removeCertificate(@RequestBody @Encrypt Certificate certificate) {
        SecurityTokenHelper.validToken(certificate);
        certificateService.deleteCertificate(certificate.getCertificateId());
        return Results.success();
    }
}
