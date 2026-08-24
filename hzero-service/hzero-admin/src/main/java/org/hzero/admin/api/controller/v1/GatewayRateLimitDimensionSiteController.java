package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.GatewayRateLimitDimensionService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/18 4:07 下午
 */
@Api(tags = {SwaggerApiConfig.GATEWAY_RATE_LIMIT_DIMENSION_SITE})
@RestController("gatewayRateLimitDimensionSiteController.v1")
@RequestMapping("/v1/dimension-configs")
public class GatewayRateLimitDimensionSiteController {

    @Autowired
    private GatewayRateLimitDimensionService gatewayRateLimitDimensionService;

    @ApiOperation(value = "维度配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rateLimitLineId", value = "限流配置ID", paramType = "query")
    })
    @ProcessLovValue(targetField = "body")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<GatewayRateLimitDimension>> list(@Encrypt @RequestParam("rateLimitLineId") Long rateLimitLineId,
                                                                @ApiIgnore @SortDefault(value = GatewayRateLimitDimension.FIElD_RATE_LIMIT_DIMENSION_ID, direction = Sort.Direction.DESC) PageRequest pageRequest){
        return Results.success(gatewayRateLimitDimensionService.pageByCondition(rateLimitLineId, pageRequest));
    }

    @ApiOperation(value = "维度配置明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rateLimitDimId", value = "维度配置ID", paramType = "path")
    })
    @ProcessLovValue(targetField = "body")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{rateLimitDimId}")
    public ResponseEntity<GatewayRateLimitDimension> detail(@Encrypt @PathVariable("rateLimitDimId") Long rateLimitDimId){
        return Results.success(gatewayRateLimitDimensionService.selectByPrimaryKey(rateLimitDimId).translate());
    }

    @ApiOperation(value = "新增维度配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<?> create(@Encrypt @RequestBody GatewayRateLimitDimension gatewayRateLimitDimension){
        return Results.success(gatewayRateLimitDimensionService.insertSelective(gatewayRateLimitDimension));
    }

    @ApiOperation(value = "更新维度配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<?> update(@Encrypt @RequestBody GatewayRateLimitDimension gatewayRateLimitDimension){
        return Results.success(gatewayRateLimitDimensionService.updateByPrimaryKeySelective(gatewayRateLimitDimension));
    }

    @ApiOperation(value = "删除维度配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> delete(@Encrypt @RequestBody GatewayRateLimitDimension gatewayRateLimitDimension){
        return Results.success(gatewayRateLimitDimensionService.deleteByCondition(gatewayRateLimitDimension));
    }

}
