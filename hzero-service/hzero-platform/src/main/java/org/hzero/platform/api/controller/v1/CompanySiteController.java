package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Company;
import org.hzero.platform.domain.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 公司信息 平台级查询API
 *
 * @author minzhen.you@hand-china.com 2018年11月13日下午7:38:43
 */
@Api(tags = PlatformSwaggerApiConfig.COMPANY_SITE)
@RestController("companySiteController.v1")
@RequestMapping("/v1/companies")
public class CompanySiteController {
    @Autowired
    private CompanyRepository companyRepository;

    @ApiOperation(value = "公司信息列表(平台级)")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<List<Company>> list(@ApiParam(value = "租户ID", required = true) @RequestParam("organizationId") Long tenantId,
                                              Company company) {
        company = ObjectUtils.defaultIfNull(company, new Company());
        company.setTenantId(tenantId);
        return Results.success(this.companyRepository.selectCompanies(company));
    }
}
