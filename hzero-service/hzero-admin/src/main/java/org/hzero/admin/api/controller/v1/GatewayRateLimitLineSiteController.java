package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.GatewayRateLimitLineDto;
import org.hzero.admin.app.service.GatewayRateLimitLineService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 网关限流设置行明细 管理 API
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Api(tags = SwaggerApiConfig.GATEWAY_RATE_LIMIT_LINE_SITE)
@RestController("gatewayRateLimitLineSiteController.v1")
@RequestMapping("/v1/gateway-rate-limit-lines")
public class GatewayRateLimitLineSiteController extends BaseController {

    @Autowired
    private GatewayRateLimitLineService gatewayRateLimitLineService;

    @ApiOperation(value = "网关限流设置行明细列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<GatewayRateLimitLine>> list(@Encrypt GatewayRateLimitLineDto gatewayRateLimitLine, @SortDefault(value =
            GatewayRateLimitLine.FIELD_RATE_LIMIT_LINE_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(gatewayRateLimitLineService.pageByCondition(gatewayRateLimitLine, pageRequest));
    }

    @ApiOperation(value = "网关限流设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{rateLimitLineId}")
    public ResponseEntity<GatewayRateLimitLine> detail(@Encrypt @PathVariable Long rateLimitLineId) {
        return Results.success(gatewayRateLimitLineService.detail(rateLimitLineId));
    }

    @ApiOperation(value = "创建网关限流设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<GatewayRateLimitLine> create(@Encrypt @RequestBody GatewayRateLimitLine gatewayRateLimitLine) {
        return Results.success(gatewayRateLimitLineService.create(gatewayRateLimitLine));
    }

    @ApiOperation(value = "修改网关限流设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<GatewayRateLimitLine> update(@Encrypt @RequestBody GatewayRateLimitLine gatewayRateLimitLine) {
        return Results.success(gatewayRateLimitLineService.updateByPrimaryKey(gatewayRateLimitLine));
    }

    @ApiOperation(value = "批量修改或者添加网关限流设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping(value = "/{rateLimitId}")
    public ResponseEntity<List<GatewayRateLimitLine>> batchUpdate(@Encrypt @RequestBody List<GatewayRateLimitLine> gatewayRateLimitLine) {
        return Results.success(gatewayRateLimitLineService.batchInsertOrUpdate(gatewayRateLimitLine));
    }

    @ApiOperation(value = "批量删除网关限流设置行明细")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/batch-delete")
    public ResponseEntity<Integer> remove(@Encrypt @RequestBody List<GatewayRateLimitLine> gatewayRateLimitLines) {
        return Results.success(gatewayRateLimitLineService.batchDelete(gatewayRateLimitLines));
    }

    @ApiOperation(value = "网关限流设置行是否允许修改")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/if-allow-change")
    public ResponseEntity<Boolean> ifAllowChange(@Encrypt @RequestParam("rateLimitLineId") Long rateLimitLineId) {
        return Results.success(gatewayRateLimitLineService.allowChange(rateLimitLineId));
    }
}
