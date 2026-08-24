package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.EmailServerService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 邮箱服务 管理 API
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Api(tags = MessageSwaggerApiConfig.EMAIL_SERVER)
@RestController("emailServerController.v1")
@RequestMapping("/v1")
public class EmailServerController extends BaseController {

    private EmailServerService emailServerService;
    private EmailServerRepository emailServerRepository;

    @Autowired
    public EmailServerController(EmailServerService emailServerService, EmailServerRepository emailServerRepository) {
        this.emailServerService = emailServerService;
        this.emailServerRepository = emailServerRepository;
    }

    @ApiOperation("查询邮箱服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "serverCode", value = "消息模板编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "消息模板名称", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    @CustomPageRequest
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/email/servers")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<EmailServer>> listEmailServer(@PathVariable("organizationId") Long organizationId,
                                                             @RequestParam(required = false) String serverCode,
                                                             @RequestParam(required = false) String serverName,
                                                             @RequestParam(required = false) Integer enabledFlag,
                                                             @ApiIgnore @SortDefault(value = EmailServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(emailServerService.listEmailServer(organizationId, serverCode, serverName, enabledFlag, true, pageRequest));
    }

    @ApiOperation("查询邮箱服务明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "邮箱服务ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/email/servers/{serverId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<EmailServer> getEmailServer(@PathVariable Long organizationId,
                                                      @Encrypt @PathVariable long serverId) {
        return Results.success(emailServerService.getEmailServer(organizationId, serverId));
    }

    @ApiOperation("邮箱服务配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "邮箱服务ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/email/servers/{serverId}/properties")
    public ResponseEntity<List<EmailProperty>> getEmailProperty(@PathVariable Long organizationId,
                                                                @Encrypt @PathVariable long serverId) {
        return Results.success(emailServerService.listEmailProperty(organizationId, serverId));
    }

    @ApiOperation("创建邮箱服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{organizationId}/email/servers")
    public ResponseEntity<EmailServer> createEmailServer(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody EmailServer emailServer) {
        emailServer.setTenantId(organizationId);
        for (EmailProperty emailProperty : emailServer.getEmailProperties()) {
            emailProperty.setTenantId(organizationId);
        }
        this.validObject(emailServer);
        return Results.created(emailServerService.createEmailServer(emailServer));
    }

    @ApiOperation("修改邮箱服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping("/{organizationId}/email/servers")
    public ResponseEntity<EmailServer> updateEmailServer(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody EmailServer emailServer) {
        emailServer.setTenantId(organizationId);
        for (EmailProperty emailProperty : emailServer.getEmailProperties()) {
            emailProperty.setTenantId(organizationId);
        }
        SecurityTokenHelper.validToken(emailServer, false);
        this.validObject(emailServer);
        emailServer.validateTenant(emailServerRepository);
        return Results.success(emailServerService.updateEmailServer(emailServer));
    }

    @ApiOperation("根据编码查询邮箱账户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverCode", value = "邮箱服务编码", paramType = "query", required = true)
    })
    @Permission(permissionWithin = true)
    @GetMapping("/email/servers/by-code")
    public ResponseEntity<List<EmailServer>> listEmailServersByCode(@RequestParam String serverCode, @RequestParam(value = "tenantId", required = false) Long tenantId) {
        return Results.success(emailServerService.listEmailServersByCode(serverCode, tenantId));
    }

    @ApiOperation("删除邮箱服务")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/email/servers")
    public ResponseEntity deleteEmailServer(@PathVariable("organizationId") Long organizationId, @Encrypt @RequestBody EmailServer emailServer) {
        emailServer.setTenantId(organizationId);
        SecurityTokenHelper.validToken(emailServer);
        emailServerService.deleteEmailServer(emailServer);
        return Results.success();
    }
}
