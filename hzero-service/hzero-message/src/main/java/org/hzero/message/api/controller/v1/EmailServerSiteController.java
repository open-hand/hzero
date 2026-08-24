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
import org.hzero.mybatis.helper.DataSecurityHelper;
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
@Api(tags = MessageSwaggerApiConfig.EMAIL_SERVER_SITE)
@RestController("emailServerSiteController.v1")
@RequestMapping("/v1/email/servers")
public class EmailServerSiteController extends BaseController {
    private EmailServerService emailServerService;

    @Autowired
    public EmailServerSiteController(EmailServerService emailServerService) {
        this.emailServerService = emailServerService;
    }

    @ApiOperation("查询邮箱服务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户ID", paramType = "query"),
            @ApiImplicitParam(name = "serverCode", value = "消息模板编码", paramType = "query"),
            @ApiImplicitParam(name = "serverName", value = "消息模板名称", paramType = "query"),
            @ApiImplicitParam(name = "enabledFlag", value = "启用标记", paramType = "query")
    })
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<EmailServer>> listEmailServer(@RequestParam(required = false) Long tenantId,
                                                             @RequestParam(required = false) String serverCode,
                                                             @RequestParam(required = false) String serverName,
                                                             @RequestParam(required = false) Integer enabledFlag,
                                                             @ApiIgnore @SortDefault(value = EmailServer.FIELD_SERVER_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(emailServerService.listEmailServer(tenantId, serverCode, serverName, enabledFlag, false, pageRequest));
    }

    @ApiOperation("查询邮箱服务明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "邮箱服务ID", paramType = "path", required = true)
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serverId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<EmailServer> getEmailServer(@Encrypt @PathVariable long serverId) {
        return Results.success(emailServerService.getEmailServer(null, serverId));
    }

    @ApiOperation("邮箱服务配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "serverId", value = "邮箱服务ID", paramType = "path")
    })
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serverId}/properties")
    public ResponseEntity<List<EmailProperty>> getEmailProperty(@Encrypt @PathVariable long serverId) {
        return Results.success(emailServerService.listEmailProperty(null, serverId));
    }

    @ApiOperation("创建邮箱服务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<EmailServer> createEmailServer(@Encrypt @RequestBody EmailServer emailServer) {
        this.validObject(emailServer);
        DataSecurityHelper.open();
        return Results.created(emailServerService.createEmailServer(emailServer));
    }

    @ApiOperation("修改邮箱服务")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<EmailServer> updateEmailServer(@Encrypt @RequestBody EmailServer emailServer) {
        SecurityTokenHelper.validToken(emailServer, false);
        this.validObject(emailServer);
        DataSecurityHelper.open();
        return Results.success(emailServerService.updateEmailServer(emailServer));
    }
}
