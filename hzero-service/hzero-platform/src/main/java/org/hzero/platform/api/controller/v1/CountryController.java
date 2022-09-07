package org.hzero.platform.api.controller.v1;


import static org.hzero.platform.domain.entity.Country.COUNTRY_ID;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.app.service.CountryService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Country;
import org.hzero.starter.keyencrypt.core.Encrypt;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * <p>
 * 国家定义接口API
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:07
 */
@Api(tags = PlatformSwaggerApiConfig.COUNTRY)
@RestController("countryController.v1")
@RequestMapping("/v1/{organizationId}")
public class CountryController extends BaseController {
    private CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @ApiOperation("分页获取所有国家")
    @GetMapping("/countries")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @CustomPageRequest
    public ResponseEntity<Page<Country>> pageCountry(@PathVariable Long organizationId,
                                                     @RequestParam(required = false) String condition,
                                                     @RequestParam(required = false) Integer enabledFlag,
                                                     @ApiIgnore @SortDefault(value = COUNTRY_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(countryService.pageCountryByCondition(organizationId, condition, enabledFlag, pageRequest));
    }

    @ApiOperation("获取所有国家")
    @GetMapping("/countries/all")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    public ResponseEntity<List<Country>> listCountry(@PathVariable Long organizationId,
                                                     @RequestParam(required = false) String condition,
                                                     @RequestParam(defaultValue = "1") Integer enabledFlag) {
        return Results.success(countryService.listCountryByCondition(organizationId, condition, enabledFlag));
    }

    @ApiOperation("查询指定国家")
    @GetMapping("/countries/{countryId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Country> queryCountry(@PathVariable @Encrypt long countryId) {
        return Results.success(countryService.queryCountry(countryId));
    }

    @ApiOperation("新增国家定义")
    @PostMapping("/country")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Country> createCountry(@PathVariable Long organizationId, @RequestBody @Encrypt Country country) {
        country.setTenantId(organizationId);
        this.validObject(country);
        return Results.created(countryService.createCountry(country));
    }

    @ApiOperation("更新国家定义")
    @PutMapping("/countries/{countryId}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<Country> updateCountry(@PathVariable Long organizationId, @PathVariable @Encrypt long countryId, @RequestBody @Encrypt Country country) {
        SecurityTokenHelper.validToken(country.setCountryId(countryId));
        country.setTenantId(organizationId);
        this.validObject(country);
        return Results.success(countryService.updateCountry(country));
    }

    @ApiOperation("批量禁用国家定义")
    @PatchMapping("/countries")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity batchDisableCountry(@RequestBody @Encrypt List<Country> countryList) {
        SecurityTokenHelper.validToken(countryList);
        this.validList(countryList, Country.PrimaryKeyValid.class);
        return Results.success(countryService.batchDisableCountry(countryList));
    }
}
