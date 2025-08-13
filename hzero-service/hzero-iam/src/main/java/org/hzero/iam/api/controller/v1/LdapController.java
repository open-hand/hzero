package org.hzero.iam.api.controller.v1;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.app.service.LdapService;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.entity.LdapSyncConfig;
import org.hzero.iam.domain.repository.LdapErrorUserRepository;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Ldap 接口
 *
 * @author bojiangzhou 2019/08/02
 */
@Api(tags = SwaggerApiConfig.LDAP)
@RestController("ldapController.v1")
@RequestMapping("/v1/{organizationId}/ldaps")
public class LdapController extends BaseController {

    private final LdapService ldapService;
    private final LdapRepository ldapRepository;
    private final LdapHistoryRepository ldapHistoryRepository;
    private final LdapErrorUserRepository ldapErrorUserRepository;

    public LdapController(LdapService ldapService, LdapRepository ldapRepository,
                    LdapHistoryRepository ldapHistoryRepository, LdapErrorUserRepository ldapErrorUserRepository) {
        this.ldapService = ldapService;
        this.ldapRepository = ldapRepository;
        this.ldapHistoryRepository = ldapHistoryRepository;
        this.ldapErrorUserRepository = ldapErrorUserRepository;
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建 Ldap")
    @PostMapping
    public ResponseEntity<Ldap> create(@PathVariable Long organizationId, @RequestBody Ldap ldap) {
        ldap.setOrganizationId(organizationId);
        this.validObject(ldap);
        return Results.success(ldapService.create(ldap));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改 Ldap")
    @PutMapping
    public ResponseEntity<Void> update(@PathVariable Long organizationId, @RequestBody Ldap ldap) {
        ldap.setOrganizationId(organizationId);
        this.validObject(ldap);
        if (ldap.getId() == null) {
            ldapService.create(ldap);
        } else {
            SecurityTokenHelper.validToken(ldap);
            ldapService.update(ldap);
        }
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "启用 Ldap")
    @PutMapping(value = "/enable")
    public ResponseEntity<Void> enableLdap(@PathVariable Long organizationId, @RequestBody Ldap ldap) {
        SecurityTokenHelper.validToken(ldap);
        ldap.setOrganizationId(organizationId);
        ldapService.enableLdap(ldap);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用 Ldap")
    @PutMapping(value = "/disable")
    public ResponseEntity<Void> disableLdap(@PathVariable Long organizationId, @RequestBody Ldap ldap) {
        SecurityTokenHelper.validToken(ldap);
        ldap.setOrganizationId(organizationId);
        ldapService.disableLdap(ldap);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询租户下的 Ldap")
    @GetMapping
    public ResponseEntity<Ldap> queryLdap(@PathVariable Long organizationId) {
        return Results.success(ldapRepository.selectLdapByTenantId(organizationId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除租户下的 Ldap")
    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long organizationId, @RequestBody Ldap ldap) {
        SecurityTokenHelper.validToken(ldap);
        ldap.setOrganizationId(organizationId);
        ldapService.delete(ldap);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "测试 Ldap 连接")
    @PostMapping("/{ldapId}/test-connect")
    public ResponseEntity<LdapConnectionDTO> testConnect(@PathVariable Long organizationId,
                                                         @Encrypt @PathVariable Long ldapId,
                    @RequestBody LdapAccountDTO ldapAccount) {
        return Results.success(ldapService.testConnect(organizationId, ldapId, ldapAccount));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "同步 Ldap 用户")
    @PostMapping("/{ldapId}/sync-users")
    public ResponseEntity<Void> syncUsers(@PathVariable Long organizationId, @Encrypt @PathVariable Long ldapId) {
        ldapService.syncLdapUser(organizationId, ldapId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据 ldap id 查询最新一条历史记录")
    @GetMapping("/{ldapId}/latest-history")
    public ResponseEntity<LdapHistory> latestHistory(@PathVariable Long organizationId, @Encrypt @PathVariable Long ldapId) {
        return Results.success(ldapHistoryRepository.queryLatestHistory(ldapId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据 ldap id 查询历史记录")
    @CustomPageRequest
    @GetMapping("/{ldapId}/history")
    public ResponseEntity<Page<LdapHistory>> pageLdapHistories(
                    @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                    @PathVariable Long organizationId, @Encrypt @PathVariable Long ldapId) {
        return Results.success(ldapHistoryRepository.pageLdapHistories(pageRequest, ldapId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ldap history id查询同步用户错误详情")
    @CustomPageRequest
    @GetMapping("/ldap-histories/{ldapHistoryId}/error-users")
    public ResponseEntity<Page<LdapErrorUser>> pageLdapHistoryErrorUsers(
                    @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                    @Encrypt @PathVariable Long ldapHistoryId, @Encrypt LdapErrorUser ldapErrorUser) {
        return Results.success(
                        ldapErrorUserRepository.pageLdapHistoryErrorUsers(pageRequest, ldapHistoryId, ldapErrorUser));
    }


    /**
     * 用于ldap同步过程中，因为不可控因素（iam服务挂掉）导致endTime为空一直在同步中的问题，该接口只是更新下endTime
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据 ldap id 更新历史记录的 endTime")
    @PutMapping("/{ldapId}/stop")
    public ResponseEntity<Void> stop(@PathVariable Long organizationId, @Encrypt @PathVariable Long ldapId) {
        ldapService.stop(ldapId);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "保存ldap定时同步用户配置")
    @PostMapping("/sync-user-config")
    public ResponseEntity<LdapSyncConfig> createLdapSyncUserConfig(@PathVariable("organizationId") Long tenantId,
                    @RequestBody LdapSyncConfig ldapSyncConfig) {
        return Results.success(ldapService.saveLdapSyncUserConfig(tenantId, ldapSyncConfig));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询ldap定时同步用户配置")
    @GetMapping("/sync-user-config")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<LdapSyncConfig> queryLdapSyncUserConfig(@PathVariable("organizationId") Long tenantId) {
        return Results.success(ldapService.queryLdapSyncUserConfig(tenantId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "保存ldap定时同步离职用户配置")
    @PostMapping("/sync-leave-config")
    public ResponseEntity<LdapSyncConfig> createLdapSyncLeaveConfig(@PathVariable("organizationId") Long tenantId,
                    @RequestBody LdapSyncConfig ldapSyncConfig) {
        return Results.success(ldapService.saveLdapSyncLeaveConfig(tenantId, ldapSyncConfig));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询ldap定时同步离职用户配置")
    @GetMapping("/sync-leave-config")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<LdapSyncConfig> queryLdapSyncLeaveConfig(@PathVariable("organizationId") Long tenantId) {
        return Results.success(ldapService.queryLdapSyncLeaveConfig(tenantId));
    }
}
