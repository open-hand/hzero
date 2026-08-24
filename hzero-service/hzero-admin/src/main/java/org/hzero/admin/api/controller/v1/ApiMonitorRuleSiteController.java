package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.ApiMonitorRuleService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.ApiMonitorRule;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 3:41 下午
 */
@Api(tags = {SwaggerApiConfig.API_MONITOR_RULE_SITE})
@RestController("apiMonitorRuleSiteController.v1")
@RequestMapping("/v1/api-monitor-rules")
public class ApiMonitorRuleSiteController {

    @Autowired
    private ApiMonitorRuleService apiMonitorRuleService;

    @ApiOperation(value = "创建api监控配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ApiMonitorRule> create(@Encrypt @RequestBody ApiMonitorRule rule) {
        return Results.success(apiMonitorRuleService.validateAndCreate(rule));
    }

    @ApiOperation(value = "删除api监控配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<?> delete(@Encrypt @RequestParam("monitorRuleIds") List<Long> monitorRuleIds) {
        return Results.success(apiMonitorRuleService.batchDelete(monitorRuleIds));
    }

    @ApiOperation(value = "分页查询api监控配置")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<ApiMonitorRule>> page(
            @Encrypt @RequestParam(value = "monitorRuleId", required = false) Long monitorRuleId,
            @RequestParam(value = "urlPattern", required = false) String urlPattern,
            @SortDefault(value = ApiMonitorRule.FIELD_API_MONITOR_RULE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(apiMonitorRuleService.pageAndSort(monitorRuleId, urlPattern, pageRequest));
    }

    @ApiOperation(value = "应用api监控配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping("apply")
    public ResponseEntity<?> apply(
            @Encrypt @RequestParam(value = "monitorRuleIds", required = false) List<Long> monitorRuleIds) {
        apiMonitorRuleService.apply(monitorRuleIds);
        return Results.success();
    }

}
