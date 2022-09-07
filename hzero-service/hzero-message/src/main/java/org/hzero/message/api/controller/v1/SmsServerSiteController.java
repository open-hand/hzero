package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.SmsServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
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

/**
 * 短信服务 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Api(tags = MessageSwaggerApiConfig.SMS_SERVER_SITE)
@RestController("smsServerSiteController.v1")
@RequestMapping("/v1/sms/servers")
public class SmsServerSiteController extends BaseController {
    private SmsServerService smsServerService;

    @Autowired
    public SmsServerSiteController(SmsServerService smsServerService) {
        this.smsServerService = smsServerService;
    }

    @ApiOperation("查询短信服务配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "serverCode", value = "服务代码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "服务名称", paramType = "query"),
            @ApiImplicitParam(name = "serverTypeCode", value = "服务类型", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<SmsServer>> listSmsServer(@RequestParam(required = false) Long tenantId,
                                                         @RequestParam(required = false) String serverCode,
                                                         @RequestParam(required = false) String serverName,
                                                         @RequestParam(required = false) String serverTypeCode,
                                                         @RequestParam(required = false) Integer enabledFlag,
                                                         @ApiIgnore @SortDefault(value = SmsServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(smsServerService.listSmsServer(tenantId, serverCode, serverName, serverTypeCode, enabledFlag, false, pageRequest));
    }

    @ApiOperation("查询短信服务配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "短信服务ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serverId}")
    public ResponseEntity<SmsServer> getSmsServer(@Encrypt @PathVariable long serverId) {
        return Results.success(smsServerService.getSmsServer(null, serverId));
    }

    @ApiOperation("创建短信服务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<SmsServer> createSmsServer(@RequestBody SmsServer smsServer) {
        this.validObject(smsServer);
        DataSecurityHelper.open();
        return Results.created(smsServerService.createSmsServer(smsServer));
    }

    @ApiOperation("修改短信服务")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<SmsServer> updateSmsServer(@Encrypt @RequestBody SmsServer smsServer) {
        SecurityTokenHelper.validToken(smsServer);
        this.validObject(smsServer);
        DataSecurityHelper.open();
        return Results.success(smsServerService.updateSmsServer(smsServer));
    }
}
