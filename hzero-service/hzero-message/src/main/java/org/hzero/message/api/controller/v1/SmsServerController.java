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
import org.hzero.message.domain.repository.SmsServerRepository;
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
@Api(tags = MessageSwaggerApiConfig.SMS_SERVER)
@RestController("smsServerController.v1")
@RequestMapping("/v1/{organizationId}/sms/servers")
public class SmsServerController extends BaseController {
    private SmsServerService smsServerService;
    private SmsServerRepository smsServerRepository;

    @Autowired
    public SmsServerController(SmsServerService smsServerService, SmsServerRepository smsServerRepository) {
        this.smsServerService = smsServerService;
        this.smsServerRepository = smsServerRepository;
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
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<SmsServer>> listSmsServer(@PathVariable("organizationId") Long organizationId,
                                                         @RequestParam(required = false) String serverCode,
                                                         @RequestParam(required = false) String serverName,
                                                         @RequestParam(required = false) String serverTypeCode,
                                                         @RequestParam(required = false) Integer enabledFlag,
                                                         @ApiIgnore @SortDefault(value = SmsServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(smsServerService.listSmsServer(organizationId, serverCode, serverName, serverTypeCode, enabledFlag, true, pageRequest));
    }

    @ApiOperation("查询短信服务配置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "短信服务ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{serverId}")
    public ResponseEntity<SmsServer> getSmsServer(@PathVariable Long organizationId,
                                                  @Encrypt @PathVariable long serverId) {
        return Results.success(smsServerService.getSmsServer(organizationId, serverId));
    }

    @ApiOperation("创建短信服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<SmsServer> createSmsServer(@PathVariable("organizationId") Long organizationId, @RequestBody SmsServer smsServer) {
        smsServer.setTenantId(organizationId);
        this.validObject(smsServer);
        return Results.created(smsServerService.createSmsServer(smsServer));
    }

    @ApiOperation("修改短信服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<SmsServer> updateSmsServer(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody SmsServer smsServer) {
        smsServer.setTenantId(organizationId);
        SecurityTokenHelper.validToken(smsServer);
        this.validObject(smsServer);
        smsServer.validateTenant(smsServerRepository);
        return Results.success(smsServerService.updateSmsServer(smsServer));
    }

    @ApiOperation("删除短信配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity deleteSmsServer(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody SmsServer smsServer) {
        smsServer.setTenantId(organizationId);
        SecurityTokenHelper.validToken(smsServer);
        smsServerService.deleteSmsServer(smsServer);
        return Results.success();
    }
}
