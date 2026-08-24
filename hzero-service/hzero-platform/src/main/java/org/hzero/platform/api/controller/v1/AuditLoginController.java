package org.hzero.platform.api.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.platform.api.dto.AuditLoginExportForOrg;
import org.hzero.platform.app.service.AuditLoginService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.AuditLogin;
import org.hzero.platform.domain.repository.AuditLoginRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 登录日志审计 管理 API
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
@Api(tags = PlatformSwaggerApiConfig.AUDIT_LOGIN)
@RestController("auditLoginController.v1")
@RequestMapping("/v1")
public class AuditLoginController extends BaseController {

    @Autowired
    private AuditLoginRepository auditLoginRepository;
    @Autowired
    private AuditLoginService auditLoginService;

    @ApiOperation(value = "租户级-分页查询登录审计信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/audit-logins")
    public ResponseEntity<Page<AuditLogin>> organizationList(@PathVariable("organizationId") Long tenantId,
                                                             AuditLogin auditLogin,
                                                             @ApiIgnore @SortDefault(value = AuditLogin.FIELD_AUDIT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        auditLogin.setTenantId(tenantId);
        return Results.success(auditLoginRepository.pageAuditLogin(pageRequest, auditLogin));
    }

    @ApiOperation(value = "个人中心-分页查询登录审计信息")
    @Permission(permissionLogin = true)
    @GetMapping("/audit-logins/self")
    public ResponseEntity<Page<AuditLogin>> selfList(@ApiIgnore @SortDefault(value = AuditLogin.FIELD_AUDIT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        AuditLogin auditLogin = new AuditLogin();
        auditLogin.setUserId(DetailsHelper.getUserDetails().getUserId());
        return Results.success(auditLoginRepository.pageAuditLogin(pageRequest, auditLogin));
    }

    @ApiOperation(value = "导出登录审计信息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{organizationId}/audit-logins/export")
    @ExcelExport(AuditLoginExportForOrg.class)
    @CustomPageRequest
    public ResponseEntity<List<AuditLoginExportForOrg>> export(ExportParam exportParam,
                                                               @PathVariable("organizationId") Long tenantId,
                                                               @Encrypt AuditLogin auditLogin,
                                                               HttpServletResponse response) {
        auditLogin.setTenantId(tenantId);
        return Results.success(auditLoginService.exportForOrg(auditLogin));
    }

    @ApiOperation(value = "查询获取可访问租户列表及登录日志")
    @Permission(permissionLogin = true)
    @GetMapping("/audit-logins/self-tenant")
    public ResponseEntity<List<AuditLogin>> selfTenantsWithLogs() {
        return Results.success(auditLoginRepository.getSelfTenantsWithLogs());
    }

    @ApiOperation(value = "审计登录日志清理")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/{organizationId}/audit-logins/clear")
    public ResponseEntity clearLog(@PathVariable Long organizationId, @RequestParam String clearType) {
        auditLoginService.clearLog(clearType, organizationId);
        return Results.success();
    }

}

