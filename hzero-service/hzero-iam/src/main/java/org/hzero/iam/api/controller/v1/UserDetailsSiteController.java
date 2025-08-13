package org.hzero.iam.api.controller.v1;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.service.user.UserDetailsService;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 当前用户详细信息管理API
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 15:31
 */
@Api(tags = SwaggerApiConfig.USER_DETAILS_SITE)
@RestController("userDetailsSiteController.v1")
@RequestMapping("/v1/users")
public class UserDetailsSiteController extends BaseController {
    private static final String TENANT_ID = "tenantId";

    private UserDetailsService userDetailsService;


    @Autowired
    public UserDetailsSiteController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @ApiOperation("查询当前用户的当前租户")
    @Permission(permissionLogin = true)
    @GetMapping("/tenant-id")
    public ResponseEntity<Map<String, Long>> readUserTenant() {
        Map<String, Long> result = new HashMap<>(1);
        result.put(TENANT_ID, userDetailsService.readUserTenant());
        return Results.success(result);
    }

    @ApiOperation("缓存当前用户的租户")
    @Permission(permissionLogin = true)
    @PutMapping("/tenant-id")
    public ResponseEntity<Map<String, Long>> storeUserTenant(@RequestParam Long tenantId) {
        userDetailsService.storeUserTenant(tenantId);
        return Results.success();
    }

    @ApiOperation("根据用户登录名刷新可访问租户列表和可选角色列表")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@RequestBody List<String> loginNameList) {
        userDetailsService.refresh(loginNameList);
        return Results.success();
    }

    @ApiOperation("切换租户角色并且重定向")
    @Permission(permissionLogin = true)
    @GetMapping("/tenant/redirect")
    public RedirectView redirect(@RequestParam(required = false) Long targetTenantId,
                                 @RequestParam(required = false) @Encrypt Long targetRoleId,
                                 @RequestParam String redirectUrl) {
        if (targetTenantId != null) {
            userDetailsService.storeUserTenant(targetTenantId);
        }
        if (targetRoleId != null) {
            userDetailsService.storeUserRole(targetRoleId);
        }
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @ApiOperation("切换租户角色并且返回重定向地址")
    @Permission(permissionLogin = true)
    @GetMapping("/tenant/redirect/page")
    public ResponseEntity<Map<String, String>> redirectPage(
            @RequestParam(required = false) Long targetTenantId,
            @RequestParam(required = false) @Encrypt Long targetRoleId,
            @RequestParam String redirectUrl) {
        if (targetTenantId != null) {
            userDetailsService.storeUserTenant(targetTenantId);
        }
        if (targetRoleId != null) {
            userDetailsService.storeUserRole(targetRoleId);
        }
        return Results.success(Collections.singletonMap("redirect", redirectUrl));
    }
}
