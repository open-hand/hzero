package org.hzero.platform.api.controller.v1;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.vo.ExportParam;
import org.hzero.platform.api.dto.AuditLoginExportForSite;
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
@Api(tags = PlatformSwaggerApiConfig.AUDIT_LOGIN_SITE)
@RestController("auditLoginSiteController.v1")
@RequestMapping("/v1/audit-logins")
public class AuditLoginSiteController extends BaseController {

    @Autowired
    private AuditLoginRepository auditLoginRepository;
    @Autowired
    private AuditLoginService auditLoginService;

    @ApiOperation(value = "平台级-分页查询登录审计信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<AuditLogin>> siteList(AuditLogin auditLogin,
                                                     @ApiIgnore @SortDefault(value = AuditLogin.FIELD_AUDIT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(auditLoginRepository.pageAuditLogin(pageRequest, auditLogin));
    }

    @ApiOperation(value = "导出登录审计信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/export")
    @ExcelExport(AuditLoginExportForSite.class)
    @CustomPageRequest
    public ResponseEntity<List<AuditLoginExportForSite>> export(ExportParam exportParam,
                                                                @Encrypt AuditLogin auditLogin,
                                                                HttpServletResponse response) {
        return Results.success(auditLoginService.exportForSite(auditLogin));
    }

    @ApiOperation(value = "审计登录日志清理")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping("/clear")
    public ResponseEntity clearLog(@RequestParam(required = false) Long tenantId, @RequestParam String clearType) {
        auditLoginService.clearLog(clearType, tenantId);
        return Results.success();
    }

}

