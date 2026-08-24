package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.ApiMonitorService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 2:08 下午
 */
@Api(tags = {SwaggerApiConfig.API_MONITOR_SITE})
@RestController("apiMonitorSiteController.v1")
@RequestMapping("/v1/api-monitors")
public class ApiMonitorSiteController extends BaseController {

    @Autowired
    private ApiMonitorService apiMonitorService;

    @ApiOperation(value = "分页查询api监控分析")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<ApiMonitor>> page(
            @Encrypt @RequestParam(value = "monitorRuleId", required = false) Long monitorRuleId,
            @RequestParam(value = "monitorUrl", required = false) String monitorUrl,
            @RequestParam(value = "monitorKey", required = false) String monitorKey,
            @SortDefault(value = ApiMonitor.FIELD_API_MONITOR_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(apiMonitorService.pageAndSort(pageRequest, monitorRuleId, monitorUrl, monitorKey));
    }

    @ApiOperation(value = "加入黑名单")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<Page<ApiMonitor>> blackList(@Encrypt @RequestParam(value = "monitorRuleId") Long monitorRuleId,
                                                      @RequestParam(value = "ip") String ip) {
        apiMonitorService.blacklist(monitorRuleId, ip);
        return Results.success();
    }

}
