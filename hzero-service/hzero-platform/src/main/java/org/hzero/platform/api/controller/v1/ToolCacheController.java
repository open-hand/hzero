package org.hzero.platform.api.controller.v1;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroConstant;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.Results;
import org.hzero.platform.api.dto.RoleDTO;
import org.hzero.platform.app.service.DatasourceInfoService;
import org.hzero.platform.app.service.EntityTableService;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.repository.ConfigRepository;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.ProfileValueRepository;
import org.hzero.platform.domain.repository.ResponseMessageRepository;
import org.hzero.platform.domain.service.CodeRuleDomainService;
import org.hzero.platform.domain.service.PromptDomainService;
import org.hzero.platform.infra.feign.HiamRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据缓存工具接口
 *
 * @author bojiangzhou
 */
@Api(PlatformSwaggerApiConfig.TOOL_CACHE)
@RequestMapping("/v1/tool/cache")
@RestController("toolCacheController.v1")
public class ToolCacheController {

    private final HiamRemoteService hiamRemoteService;
    private final ProfileValueRepository profileValueRepository;
    private final ConfigRepository configRepository;
    private final PromptDomainService promptDomainService;
    private final CodeRuleDomainService codeRuleDomainService;
    private final PermissionRangeRepository permissionRangeRepository;
    private final DatasourceInfoService datasourceRelService;
    private final EntityTableService entityTableService;
    private final ResponseMessageRepository messageRepository;
    private final LovService lovService;

    @Autowired
    public ToolCacheController(HiamRemoteService hiamRemoteService,
                               ProfileValueRepository profileValueRepository,
                               ConfigRepository configRepository,
                               PromptDomainService promptDomainService,
                               CodeRuleDomainService codeRuleDomainService,
                               PermissionRangeRepository permissionRangeRepository,
                               DatasourceInfoService datasourceRelService,
                               EntityTableService entityTableService,
                               ResponseMessageRepository messageRepository,
                               LovService lovService) {
        this.hiamRemoteService = hiamRemoteService;
        this.profileValueRepository = profileValueRepository;
        this.configRepository = configRepository;
        this.promptDomainService = promptDomainService;
        this.codeRuleDomainService = codeRuleDomainService;
        this.permissionRangeRepository = permissionRangeRepository;
        this.datasourceRelService = datasourceRelService;
        this.entityTableService = entityTableService;
        this.messageRepository = messageRepository;
        this.lovService = lovService;
    }


    @ApiOperation(value = "缓存 配置维护 信息到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/profile")
    public ResponseEntity<Void> initProfile() {
        checkSuperAdmin();
        profileValueRepository.initAllProfileValueToRedis();
        return Results.success();
    }

    @ApiOperation(value = "缓存 系统配置 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/config")
    public ResponseEntity<Void> initConfig() {
        checkSuperAdmin();
        configRepository.initAllConfigToRedis();
        return Results.success();
    }

    @ApiOperation(value = "缓存 平台多语言 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/prompt")
    public ResponseEntity<Void> initPrompt() {
        checkSuperAdmin();
        promptDomainService.initCache();
        return Results.success();
    }

    @ApiOperation(value = "缓存 编码规则 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/code-rule")
    public ResponseEntity<Void> initCodeRule() {
        checkSuperAdmin();
        codeRuleDomainService.initCodeRuleCache();
        return Results.success();
    }

    @ApiOperation(value = "缓存 数据权限 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/permission-range")
    public ResponseEntity<Void> initPermissionRange() {
        checkSuperAdmin();
        permissionRangeRepository.initAllData();
        return Results.success();
    }

    @ApiOperation(value = "缓存 数据源配置 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/datasource")
    public ResponseEntity<Void> initDatasource() {
        checkSuperAdmin();
        datasourceRelService.initAllData();
        return Results.success();
    }

    @ApiOperation(value = "缓存 实体表 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/entity-table")
    public ResponseEntity<Void> initEntityTable() {
        checkSuperAdmin();
        entityTableService.initAllDataToRedis();
        return Results.success();
    }

    @ApiOperation(value = "缓存 返回消息 到Redis")
    @Permission(permissionLogin = true)
    @PostMapping("/return-message")
    public ResponseEntity<Void> initResponseMessage() {
        checkSuperAdmin();
        messageRepository.initAllReturnMessageToRedis();
        return Results.success();
    }

    @ApiOperation(value = "刷新值集-清除Redis缓存")
    @Permission(permissionLogin = true)
    @PostMapping("/lov-all")
    public ResponseEntity<Void> initLovCache() {
        checkSuperAdmin();
        lovService.initLovCache();
        return Results.success();
    }

    private void checkSuperAdmin() {
        CustomUserDetails self = Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new);
        RoleDTO role = hiamRemoteService.getRole(self.getTenantId(), self.getRoleId());
        if (!StringUtils.equalsAny(role.getCode(), HZeroConstant.RoleCode.SITE, HZeroConstant.RoleCode.TENANT)) {
            throw new CommonException("hiam.warn.operation.onlySupperAdminAllowed");
        }
    }



}
