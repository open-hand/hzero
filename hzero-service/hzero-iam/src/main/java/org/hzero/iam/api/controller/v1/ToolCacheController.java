package org.hzero.iam.api.controller.v1;

import org.hzero.core.util.Results;
import org.hzero.iam.app.service.OpenAppService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.AuthorityDomainService;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据缓存工具接口
 *
 * @author bojiangzhou
 */
@Api(tags = SwaggerApiConfig.TOOL_CACHE)
@RequestMapping("/v1/tool/cache")
@RestController("hiamToolCacheController.v1")
public class ToolCacheController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private LdapRepository ldapRepository;
    @Autowired
    private PasswordPolicyRepository passwordPolicyRepository;
    @Autowired
    private OpenAppService openAppService;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private AuthorityDomainService authorityDomainService;


    @ApiOperation(value = "缓存 User 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/user")
    public ResponseEntity<Void> initUser() {
        UserUtils.checkSuperAdmin();
        userRepository.initUsers();
        return Results.success();
    }

    @ApiOperation(value = "缓存 Client 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/client")
    public ResponseEntity<Void> initClient() {
        UserUtils.checkSuperAdmin();
        clientRepository.initCacheClient();
        return Results.success();
    }

    @ApiOperation(value = "缓存 Ldap 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/ldap")
    public ResponseEntity<Void> initLdap() {
        UserUtils.checkSuperAdmin();
        ldapRepository.initCacheLdap();
        return Results.success();
    }

    @ApiOperation(value = "缓存 PasswordPolicy 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/password-policy")
    public ResponseEntity<Void> initPasswordPolicy() {
        UserUtils.checkSuperAdmin();
        passwordPolicyRepository.initCachePasswordPolicy();
        return Results.success();
    }

    @ApiOperation(value = "缓存 OpenApp 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/open-app")
    public ResponseEntity<Void> initOpenApp() {
        UserUtils.checkSuperAdmin();
        openAppService.initCacheOpenApp();
        return Results.success();
    }

    @ApiOperation(value = "缓存Domain域名信息 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/domain")
    public ResponseEntity<Void> initDomain() {
        UserUtils.checkSuperAdmin();
        domainRepository.initCacheDomain();
        return Results.success();
    }


    @ApiOperation(value = "缓存单据权限信息 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/doc-type")
    public ResponseEntity<Void> initDocType() {
        UserUtils.checkSuperAdmin();
        authorityDomainService.initDocAuthCache();
        return Results.success();
    }

}
