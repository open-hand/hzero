package org.hzero.admin.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.api.dto.ApiLimitDTO;
import org.hzero.admin.app.service.ApiLimitService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 2:08 下午
 */
@Api(tags = {SwaggerApiConfig.API_LIMIT_SITE})
@RestController("apiLimitSiteController.v1")
@RequestMapping("/v1/api-limits")
public class ApiLimitSiteController extends BaseController {

    @Autowired
    private ApiLimitService apiLimitService;

    @ApiOperation(value = "创建或更新接口限制")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<ApiLimitDTO> createOrUpdate(@Encrypt @RequestBody ApiLimitDTO limit) {
        return Results.success(apiLimitService.createOrUpdate(limit));
    }

    @ApiOperation(value = "编辑查看接口限制")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<ApiLimitDTO> detail(@Encrypt @RequestParam("monitorRuleId") Long monitorRuleId) {
        return Results.success(apiLimitService.detail(monitorRuleId));
    }

}
