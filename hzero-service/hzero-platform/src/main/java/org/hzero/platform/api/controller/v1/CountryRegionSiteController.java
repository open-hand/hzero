package org.hzero.platform.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Pair;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.AddressCountryRegionDTO;
import org.hzero.platform.api.dto.AddressCountryRegionParamDTO;
import org.hzero.platform.api.dto.CountryRegionDTO;
import org.hzero.platform.app.service.CountryRegionService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 国家地区组合查询API
 * </p>
 *
 * @author qingsheng.chen 2018/7/3 星期二 10:31
 */
@Api(tags = PlatformSwaggerApiConfig.COUNTRY_SITE)
@RestController("countryRegionSiteController.v1")
@RequestMapping("/v1")
public class CountryRegionSiteController {
    private CountryRegionService countryRegionService;

    @Autowired
    public CountryRegionSiteController(CountryRegionService countryRegionService) {
        this.countryRegionService = countryRegionService;
    }

    @GetMapping("/country/region")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<CountryRegionDTO> queryCountryRegion(@RequestParam Long countryId, @RequestParam(required = false) Long regionId) {
        return Results.success(countryRegionService.queryCountryRegion(countryId, regionId));
    }

    @PostMapping("/countries/regions")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<List<CountryRegionDTO>> listCountryRegion(@RequestBody Set<Pair<Long, Long>> countryRegionIdList) {
        return Results.success(countryRegionService.listCountryRegion(countryRegionIdList));
    }

    @ApiOperation("查询国家、地区")
    @PostMapping("/countries/regions/address")
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    public ResponseEntity<AddressCountryRegionDTO> queryAddressCountryRegion(@RequestBody AddressCountryRegionParamDTO addressCountryRegionParam) {
        return Results.success(countryRegionService.queryAddressCountryRegion(addressCountryRegionParam));
    }
}
