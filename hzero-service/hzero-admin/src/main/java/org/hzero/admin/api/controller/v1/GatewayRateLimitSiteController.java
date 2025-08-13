

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
import io.swagger.annotations.ApiParam;
import org.hzero.admin.api.dto.GatewayRateLimitDto;
import org.hzero.admin.app.service.GatewayRateLimitService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.GatewayRateLimit;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 网关限流设置 管理 API
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Api(tags = SwaggerApiConfig.GATEWAY_RATE_LIMIT_SITE)
@RestController("gatewayRateLimitSiteController.v1")
@RequestMapping("/v1/gateway-rate-limits")
public class GatewayRateLimitSiteController extends BaseController {

    @Autowired
    private GatewayRateLimitService gatewayRateLimitService;

    @ApiOperation(value = "网关限流设置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gatewayRateLimit", value = "查询限制条件", paramType = "query"),
    })
    @ProcessLovValue(targetField = "body")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<GatewayRateLimit>> list(@Encrypt GatewayRateLimit gatewayRateLimit,
                                                       @ApiIgnore @SortDefault(value = GatewayRateLimit.FIELD_RATE_LIMIT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(gatewayRateLimitService.pageByCondition(gatewayRateLimit, pageRequest));
    }

    @ApiOperation(value = "网关限流设置明细")
    @ProcessLovValue(targetField = "body")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{rateLimitId}")
    public ResponseEntity<GatewayRateLimit> detail(@Encrypt @ApiParam("限流Id") @PathVariable("rateLimitId") Long rateLimitId) {
        return Results.success(gatewayRateLimitService.selectByPrimaryKey(rateLimitId));
    }

    @ApiOperation(value = "创建网关限流设置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/create")
    public ResponseEntity<GatewayRateLimit> create(@Encrypt @ApiParam("限流规则实体") @RequestBody GatewayRateLimit gatewayRateLimit) {
        return Results.success(gatewayRateLimitService.insertSelective(gatewayRateLimit));
    }

    @ApiOperation(value = "修改网关限流设置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<GatewayRateLimit> update(@Encrypt @ApiParam("限流规则实体") @RequestBody GatewayRateLimit gatewayRateLimit) {
        return Results.success(gatewayRateLimitService.updateByPrimaryKeySelective(gatewayRateLimit));
    }

    @ApiOperation(value = "批量修改或者创建网关限流设置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<List<GatewayRateLimit>> batchUpdate(@Encrypt @ApiParam("限流规则实体列表") @RequestBody List<GatewayRateLimit> gatewayRateLimit) {
        this.validList(gatewayRateLimit);
        return Results.success(gatewayRateLimitService.batchInsertOrUpdate(gatewayRateLimit));
    }

    @ApiOperation(value = "头行结构明细查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rateLimitId", value = "限流头Id", paramType = "path"),
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{rateLimitId}/lines")
    public ResponseEntity<GatewayRateLimitDto> queryDetail(@Encrypt @PathVariable("rateLimitId") Long rateLimitId,
                                                           @ApiIgnore PageRequest pageRequest) {
        return Results.success(gatewayRateLimitService.queryDetail(rateLimitId, pageRequest));
    }

    @ApiOperation(value = "头行保存")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/detail/save")
    public ResponseEntity<GatewayRateLimitDto> saveDetail(@Encrypt @RequestBody GatewayRateLimitDto gatewayRateLimit) {
        return Results.success(gatewayRateLimitService.saveDetail(gatewayRateLimit));
    }

    @ApiOperation(value = "刷新限流规则到配置中心")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/config/refresh")
    public ResponseEntity refresh(@Encrypt @RequestBody List<GatewayRateLimit> gatewayRateLimitList) {
        gatewayRateLimitService.refresh(gatewayRateLimitList);
        return Results.success();
    }
}
