package org.hzero.platform.api.controller.v1;

import java.util.List;
import java.util.Set;

import org.hzero.core.util.Pair;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.AddressCountryRegionDTO;
import org.hzero.platform.api.dto.AddressCountryRegionParamDTO;
import org.hzero.platform.api.dto.CountryRegionDTO;
import org.hzero.platform.app.service.CountryRegionService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 国家地区组合查询API
 * </p>
 *
 * @author qingsheng.chen 2018/7/3 星期二 10:31
 */
@Api(tags = PlatformSwaggerApiConfig.COUNTRY)
@RestController("countryRegionController.v1")
@RequestMapping("/v1/{organizationId}")
public class CountryRegionController {
    private CountryRegionService countryRegionService;

    @Autowired
    public CountryRegionController(CountryRegionService countryRegionService) {
        this.countryRegionService = countryRegionService;
    }

    @GetMapping("/country/region")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<CountryRegionDTO> queryCountryRegion(@PathVariable long organizationId,
                                                               @RequestParam @Encrypt Long countryId,
                                                               @RequestParam(required = false) @Encrypt Long regionId) {
        return Results.success(countryRegionService.queryCountryRegion(countryId, regionId));
    }

    @PostMapping("/countries/regions")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<List<CountryRegionDTO>> listCountryRegion(@PathVariable long organizationId,
                                                                    @RequestBody Set<Pair<Long, Long>> countryRegionIdList) {
        return Results.success(countryRegionService.listCountryRegion(countryRegionIdList));
    }

    @ApiOperation("查询国家、地区")
    @PostMapping("/countries/regions/address")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<AddressCountryRegionDTO> queryAddressCountryRegion(@RequestBody @Encrypt AddressCountryRegionParamDTO addressCountryRegionParam) {
        return Results.success(countryRegionService.queryAddressCountryRegion(addressCountryRegionParam));
    }
}
