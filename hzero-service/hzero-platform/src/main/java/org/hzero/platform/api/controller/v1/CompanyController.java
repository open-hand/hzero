package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.CompanyDTO;
import org.hzero.platform.app.assembler.CompanyAssembler;
import org.hzero.platform.app.service.CompanyService;
import org.hzero.platform.app.service.CountryRegionService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Company;
import org.hzero.platform.domain.repository.CompanyRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 公司信息 管理 API
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Api(tags = PlatformSwaggerApiConfig.COMPANY)
@RestController("companyController.v1")
@RequestMapping("/v1/{organizationId}/companies")
public class CompanyController extends BaseController {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CountryRegionService countryRegionService;

    @ApiOperation(value = "公司信息列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<Company>> list(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                              Company company) {
        company = ObjectUtils.defaultIfNull(company, new Company());
        company.setTenantId(tenantId);
        return Results.success(this.companyRepository.selectCompanies(company));
    }

    @ApiOperation(value = "根据ID查询公司")
    @Permission(permissionLogin = true)
    @GetMapping("/{companyId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<CompanyDTO> selectByPrimaryKey(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                         @ApiParam(value = "公司ID", required = true) @PathVariable @Encrypt Long companyId) {
        return Results.success(CompanyAssembler.assembleCompanyInfo(this.companyRepository.selectByPrimaryKey(companyId), this.countryRegionService));
    }

    @ApiOperation(value = "根据编码查询公司")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/by-number/{companyNumber}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<CompanyDTO> selectByNum(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                  @ApiParam(value = "公司编码", required = true) @PathVariable String companyNumber) {
        return Results.success(CompanyAssembler.assembleCompanyInfo(this.companyRepository.selectByNumber(companyNumber), this.countryRegionService));
    }

    @ApiOperation(value = "批量新建或更新公司")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/batch")
    public ResponseEntity<List<Company>> batchInsertOrUpdate(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                             @RequestBody @Encrypt List<Company> companies) {
        Assert.notNull(companies, BaseConstants.ErrorCode.DATA_INVALID);
        for (Company company : companies) {
            company.complementLongTermFlag();
            company.setTenantId(tenantId);
            this.validObject(company);
        }
        companies = this.companyService.batchInsertOrUpdate(companies);
        return Results.success(companies);
    }

    @ApiOperation(value = "新建或更新公司")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Company> insertOrUpdate(@ApiParam(value = "租户ID", required = true) @PathVariable("organizationId") Long tenantId,
                                                  @RequestBody @Encrypt Company company) {
        Assert.notNull(company, BaseConstants.ErrorCode.DATA_INVALID);
        company.complementLongTermFlag();
        this.validObject(company);
        company.setTenantId(tenantId);
        company = this.companyService.insertOrUpdate(company);
        return Results.success(company);
    }

    @ApiOperation(value = "启用公司")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/enable")
    public ResponseEntity<Company> enable(@RequestBody @Encrypt Company company) {
        company = this.companyService.enable(company);
        return Results.success(company);
    }

    @ApiOperation(value = "禁用公司")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/disable")
    public ResponseEntity<Company> disable(@RequestBody @Encrypt Company company) {
        company = this.companyService.disable(company);
        return Results.success(company);
    }

    @ApiOperation(value = "查询当前用户所属公司")
    @Permission(permissionLogin = true)
    @GetMapping("/list-by-current-user")
    public ResponseEntity<List<Company>> listByUser(@ApiParam(value = "租户ID", required = true)
                                                    @PathVariable("organizationId") Long tenantId,
                                                    @ApiParam(value = "公司编码")
                                                    @RequestParam(required = false) String companyNum,
                                                    @ApiParam(value = "公司名")
                                                    @RequestParam(required = false) String companyName,
                                                    @ApiParam(value = "是否启用")
                                                    @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(companyService.listByUser(tenantId, companyNum, companyName, enabledFlag));
    }

    @ApiIgnore
    @ApiOperation(value = "删除公司信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> remove(
            @PathVariable @Encrypt Long companyId
    ) {
        this.companyRepository.deleteByPrimaryKey(companyId);
        return Results.success();
    }

}
