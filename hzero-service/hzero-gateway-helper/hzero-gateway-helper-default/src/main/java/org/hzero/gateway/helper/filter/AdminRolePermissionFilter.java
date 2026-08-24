package org.hzero.gateway.helper.filter;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.common.HZeroConstant;
import org.hzero.core.util.UrlUtils;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.domain.vo.RoleVO;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.infra.mapper.PermissionPlusMapper;
import org.hzero.gateway.helper.service.CustomPermissionCheckService;

/**
 * 超级角色的权限校验，超级角色不校验角色-API关系
 */
@Component
public class AdminRolePermissionFilter implements HelperFilter, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRolePermissionFilter.class);

    private final PermissionPlusMapper permissionPlusMapper;

    /**
     * 平台超级管理员ID
     */
    private Long siteSuperAdminRoleId = -1L;
    /**
     * 租户超级管理员ID
     */
    private Long tenantSuperAdminRoleId = -1L;

    private List<String> parameterTenantId;
    private boolean checkTenant;

    private CustomPermissionCheckService customPermissionCheckService;

    public AdminRolePermissionFilter(PermissionPlusMapper permissionPlusMapper,
                                     GatewayHelperProperties properties,
                                     CustomPermissionCheckService customPermissionCheckService) {
        this.permissionPlusMapper = permissionPlusMapper;
        this.customPermissionCheckService = customPermissionCheckService;
        this.parameterTenantId = properties.getFilter().getCommonRequest().getParameterTenantId();
        this.checkTenant = properties.getFilter().getCommonRequest().isCheckTenant();
    }


    @Override
    public int filterOrder() {
        return 80;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    /**
     * 校验成员是否拥有超级管理员角色，如果有则不用校验角色权限
     *
     * <p>
     * 1、首先校验成员分配的角色是否在有效期内，这里只校验超级角色的有效期；
     * 比如在角色合并时，有多个角色，如果超级角色已过期，其它角色没过期，则会进入下一个过滤器校验角色权限；
     * 如果只有一个角色且为超级角色，且超级角色过期了，则返回角色过期。
     * <p>
     * 2、接着校验API层级与角色层级是否一致
     *
     */
    @Override
    public boolean run(RequestContext context) {
        PermissionDO permission = context.getPermission();
        CustomUserDetails details = context.getCustomUserDetails();

        Long memberId = null;
        List<Long> roleIds = Collections.emptyList();
        String memberType = null;
        if (details.getClientId() != null) {
            memberId = details.getClientId();
            roleIds = details.getRoleIds();
            memberType = "client";
        } else if (details.getUserId() != null) {
            memberId = details.getUserId();
            roleIds = details.roleMergeIds();
            memberType = "user";
        }

        if (CollectionUtils.isEmpty(roleIds)) {
            context.response.setStatus(CheckState.ROLE_IS_EMPTY);
            context.response.setMessage("Member [" + memberId + "] have no roles");
            return false;
        }

        boolean isSiteSuperRole = roleIds.contains(siteSuperAdminRoleId);
        boolean isTenantSuperRole = roleIds.contains(tenantSuperAdminRoleId);

        // 非平台超级管理员或租户超级管理员
        if (!(isSiteSuperRole || isTenantSuperRole)) {
            return true;
        }

        // 分配的超级角色是否过期
        int availableCount = 0;
        if (isSiteSuperRole) {
            availableCount = permissionPlusMapper.countAvailableRole(memberId, memberType, siteSuperAdminRoleId);
        } else {
            availableCount = permissionPlusMapper.countAvailableRole(memberId, memberType, tenantSuperAdminRoleId);
        }

        LOGGER.debug("Admin role check: memberId={}, memberType={}, roleIds={}, availableRoleCount={}", memberId, memberType, roleIds, availableCount);

        if (availableCount < 1) {
            if (roleIds.size() > 1) {
                return true;
            } else {
                context.response.setStatus(CheckState.MEMBER_ROLE_EXPIRED);
                context.response.setMessage("MemberRole expired, roleId: " + roleIds);
                return false;
            }
        }

        boolean lov = StringUtils.isNotEmpty(context.getLovCode());

        if (lov) {
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Have access to this lov : " + context.getLovCode());
            return false;
        }

        // 角色层级与权限层级是否匹配
        if (isSiteSuperRole) {
            if (!ResourceLevel.SITE.value().equals(permission.getFdLevel())) {
                context.response.setStatus(CheckState.PERMISSION_LEVEL_MISMATCH);
                context.response.setMessage("Site role no access to this 'non-site-level' interface, permission: " + context.getPermission());
            }
            else {
                context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
                context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
            }
        } else {
            if (!StringUtils.equalsAny(permission.getFdLevel(), ResourceLevel.ORGANIZATION.value(), ResourceLevel.PROJECT.value())) {
                context.response.setStatus(CheckState.PERMISSION_LEVEL_MISMATCH);
                context.response.setMessage("Tenant role no access to this 'non-tenant-level' interface, permission: " + context.getPermission());
            } else {
                if (ResourceLevel.ORGANIZATION.value().equals(permission.getFdLevel())) {
                    checkTenantPermission(context, permission.getPath());
                } else {
                    checkCustomLevelPermission(context, permission.getPath());
                }
            }
        }

        return false;
    }

    private void checkTenantPermission(final RequestContext context, final String matchPath) {
        Long tenantId = UrlUtils.parseLongValueFromUri(context.getTrueUri(), matchPath, parameterTenantId);
        if (checkTenant) {
            if (tenantId == null) {
                context.response.setStatus(CheckState.API_ERROR_ORG_ID);
                context.response.setMessage("Organization interface must have 'organizationId' or 'organization_id' in path");
            } else {
                boolean accessOrg = context.getCustomUserDetails().getTenantIds().contains(tenantId);
                if (accessOrg) {
                    context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
                    context.response.setMessage("Have access to this 'organization-level' interface, permission: " + context.getPermission());
                } else {
                    context.response.setStatus(CheckState.PERMISSION_NOT_PASS_ORG);
                    context.response.setMessage("No access to this this organization, organizationId: " + tenantId);
                }
            }
        } else {
            // 不检查租户ID，直接PASS
            context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
            context.response.setMessage("Do not check organizationId, have access to this 'organization-level' interface, permission: " + context.getPermission());
        }
    }

    private void checkCustomLevelPermission(final RequestContext context, final String matchPath) {
        if (customPermissionCheckService != null) {
            customPermissionCheckService.checkPermission(context);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        List<RoleVO> superRoles = permissionPlusMapper.selectSuperAdminRole();

        RoleVO tenantSuperRole = superRoles.stream().filter(r -> HZeroConstant.RoleCode.TENANT.equals(r.getCode())).findFirst().orElse(null);
        RoleVO siteSuperRole = superRoles.stream().filter(r -> HZeroConstant.RoleCode.SITE.equals(r.getCode())).findFirst().orElse(null);

        // 租户超级管理员
        if (tenantSuperRole != null) {
            tenantSuperAdminRoleId = tenantSuperRole.getId();
        } else {
            throw new IllegalStateException("Tenant super admin role not found. roleCode is " + HZeroConstant.RoleCode.TENANT);
        }

        // 平台超级管理员
        if (siteSuperRole != null) {
            siteSuperAdminRoleId = siteSuperRole.getId();
        } else {
            throw new IllegalStateException("Site super admin role not found. roleCode is " + HZeroConstant.RoleCode.SITE);
        }
    }
}
