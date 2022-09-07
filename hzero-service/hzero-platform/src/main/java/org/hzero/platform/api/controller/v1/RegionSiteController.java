package org.hzero.platform.api.controller.v1;

import java.util.Collections;
import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.RegionDTO;
import org.hzero.platform.app.service.RegionService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.Region;
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
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 地区定义接口API
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 9:15
 */
@Api(tags = PlatformSwaggerApiConfig.REGION_SITE)
@RestController("regionSiteController.v1")
@RequestMapping("/v1/countries")
public class RegionSiteController extends BaseController {
    private RegionService regionService;

    @Autowired
    public RegionSiteController(RegionService regionService) {
        this.regionService = regionService;
    }

    @ApiOperation("查询国家下地区定义，使用树状结构返回")
    @GetMapping("/{countryId}/regions")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    public ResponseEntity<List<RegionDTO>> treeRegionWithParent(@PathVariable @Encrypt long countryId,
                                                                @RequestParam(required = false) String condition,
                                                                @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(regionService.treeRegionWithParent(countryId, condition, enabledFlag));
    }

    @ApiOperation("查询国家/地区下的地区列表")
    @GetMapping("/regions")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    public ResponseEntity<List<Region>> listRegion(@RequestParam(required = false) @Encrypt Long countryId,
                                                   @RequestParam(required = false) @Encrypt Long regionId) {
        if (countryId == null && regionId == null) {
            return Results.success(Collections.emptyList());
        }
        return Results.success(regionService.listRegion(countryId, regionId));
    }

    @ApiOperation("查询指定地区")
    @GetMapping("/regions/{regionId}")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Region> queryRegion(@PathVariable @Encrypt long regionId) {
        return Results.success(regionService.queryRegion(regionId));
    }

    @ApiOperation("新增地区定义")
    @PostMapping("/{countryId}/region")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Region> createRegion(@PathVariable @Encrypt long countryId, @RequestBody @Encrypt Region region) {
        region.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        this.validObject(region);
        return Results.created(regionService.createRegion(region.setCountryId(countryId)));
    }

    @ApiOperation("更新地区定义")
    @PutMapping("/{countryId}/regions/{regionId}")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Region> updateRegion(@PathVariable @Encrypt long countryId,
                                               @PathVariable @Encrypt long regionId,
                                               @RequestBody @Encrypt Region region) {
        SecurityTokenHelper.validToken(region.setRegionId(regionId).setCountryId(countryId));
        region.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        this.validObject(region);
        return Results.success(regionService.updateRegion(region));
    }

    @ApiOperation("禁用/启用地区定义，影响下级")
    @PutMapping("/regions/{regionId}")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<List<Region>> disableOrEnableRegion(@PathVariable @Encrypt long regionId, @RequestBody @Encrypt Region region) {
        SecurityTokenHelper.validToken(region.setRegionId(regionId));
        return Results.success(regionService.disableOrEnable(region));
    }

    @ApiOperation("查询国家下地区定义，使用树状懒加载结构返回")
    @GetMapping("/{countryId}/regions/lazy-tree")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<List<RegionDTO>> lazyTreeRegion(@PathVariable @Encrypt long countryId,
                                                          @RequestParam(required = false) @Encrypt Long regionId,
                                                          @RequestParam(required = false) Integer enabledFlag) {
        return Results.success(regionService.lazyTreeRegion(BaseConstants.DEFAULT_TENANT_ID, countryId, regionId, enabledFlag));
    }

    @ApiOperation("分页查询国家下地区定义")
    @GetMapping("/{countryId}/regions/list")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<Page<RegionDTO>> pageTreeRegion(@PathVariable @Encrypt long countryId,
                                                          @RequestParam(required = false) String condition,
                                                          @RequestParam(required = false) Integer enabledFlag,
                                                          @ApiIgnore @SortDefault(value = "levelPath", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(regionService.pageTreeRegion(BaseConstants.DEFAULT_TENANT_ID, countryId, condition, enabledFlag, pageRequest));
    }

    @ApiOperation("批量新增地区定义")
    @PostMapping("/{countryId}/regions")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<List<Region>> batchCreateRegion(@PathVariable @Encrypt long countryId, @RequestBody @Encrypt List<Region> regions) {
        regions.forEach(region -> {
            region.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
            region.setCountryId(countryId);
        });
        this.validList(regions);
        return Results.created(regionService.batchCreateRegion(regions));
    }
}
