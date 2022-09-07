package org.hzero.gateway.helper.filter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseHeaders;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ServerRequestUtils;
import org.hzero.core.util.UrlUtils;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.domain.PermissionCheckDTO;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.infra.mapper.PermissionPlusMapper;
import org.hzero.gateway.helper.service.CustomPermissionCheckService;


/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 *
 * @author bojiangzhou Mark: 路径上租户API支持 organizationId 参数；角色权限查询权限变更。
 */
@Component
public class CommonRequestCheckFilter implements HelperFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRequestCheckFilter.class);
    private static final long NOT_LOGIN = Long.MIN_VALUE;

    private final boolean enable;
    private final boolean checkTenant;
    private final boolean checkCurrentRole;
    private final List<String> parameterTenantId;

    private final PermissionPlusMapper permissionPlusMapper;
    private final RedisHelper redisHelper;
    private final CustomPermissionCheckService customPermissionCheckService;

    public CommonRequestCheckFilter(PermissionPlusMapper permissionPlusMapper,
                                    RedisHelper redisHelper,
                                    GatewayHelperProperties properties,
                                    CustomPermissionCheckService customPermissionCheckService) {
        this.permissionPlusMapper = permissionPlusMapper;
        this.redisHelper = redisHelper;
        this.customPermissionCheckService = customPermissionCheckService;

        this.enable = properties.getFilter().getCommonRequest().isEnabled();
        this.checkTenant = properties.getFilter().getCommonRequest().isCheckTenant();
        this.checkCurrentRole = properties.getFilter().getCommonRequest().isCheckCurrentRole();
        this.parameterTenantId = properties.getFilter().getCommonRequest().getParameterTenantId();
    }

    @Override
    public int filterOrder() {
        return 90;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        if (!enable){
            context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
            context.response.setMessage("Not enable common request check.");
        }
        return enable;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDO permission = context.getPermission();
        CustomUserDetails details = context.getCustomUserDetails();

        Long memberId = null;
        String memberType = null;
        List<Long> roleIds = null;
        Long tenantId = details.getTenantId();
        List<Long> sourceIds;
        String permissionCode = permission.getCode();
        String sourceType;

        boolean lov = StringUtils.isNotEmpty(context.getLovCode());
        if (lov) {
            permissionCode = context.getLovCode();
            sourceType = null;
        } else {
            // 租户级API或项目级API都属于租户级
            sourceType = StringUtils.equals(ResourceLevel.SITE.value(), permission.getFdLevel()) ?
                    ResourceLevel.SITE.value() : ResourceLevel.ORGANIZATION.value();
        }

        if (details.getClientId() != null) {
            memberId = details.getClientId();
            memberType = "client";
            // 客户端默认是当前租户下的所有角色
            roleIds = details.getRoleIds();
        }
        else if (details.getUserId() != null) {
            memberId = details.getUserId();
            memberType = "user";
            // 用户取合并的角色
            roleIds = details.roleMergeIds();
        }

        Assert.notNull(memberId, "memberId is null");

        PermissionCheckDTO queryDTO = new PermissionCheckDTO(memberId, memberType, tenantId, roleIds,
                permissionCode, sourceType, checkCurrentRole);

        LOGGER.debug("Common request check: {}", queryDTO);

        sourceIds = permissionPlusMapper.selectSourceIdsByMemberAndRole(queryDTO);

        if (sourceIds.isEmpty()) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS);
            context.response.setMessage("No access to this interface or lov");
        } else {
            if (lov) {
                context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
                context.response.setMessage("Have access to this lov : " + context.getLovCode());
            } else {
                if (ResourceLevel.SITE.value().equals(permission.getFdLevel())) {
                    context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
                    context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
                } else if (ResourceLevel.ORGANIZATION.value().equals(permission.getFdLevel())) {
                    checkTenantPermission(context, sourceIds);
                }

                if (context.response.getStatus().getValue() >= 300) {
                    return false;
                }

                customPermissionCheck(context);
            }
        }

        return true;
    }

    private void checkTenantPermission(final RequestContext context, final List<Long> sourceIds) {
        if (checkTenant) {
            Long tenantId = getTenantId(context);
            if (tenantId == null) {
                context.response.setStatus(CheckState.API_ERROR_ORG_ID);
                context.response.setMessage("Organization interface must have 'organizationId' or 'organization_id' in path");
            } else {
                boolean accessOrg = sourceIds.contains(tenantId) || isAllowedAccess(context.getCustomUserDetails().getUserId(), tenantId);
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
            LOGGER.debug("Do not check organizationId, have access to this 'organization-level' interface, permission: {}", context.getPermission());
        }
    }

    private Long getTenantId(final RequestContext context) {
        Long tenantId = UrlUtils.parseLongValueFromUri(context.getTrueUri(), context.getPermission().getPath(), parameterTenantId);
        if (tenantId != null) {
            return tenantId;
        }
        String value = ServerRequestUtils.getHeaderValue(context.getServletRequest(), BaseHeaders.H_TENANT_ID);
        if (StringUtils.isNotBlank(value)) {
            return Long.parseLong(value);
        }
        return null;
    }

    private void customPermissionCheck(final RequestContext context) {
        if (customPermissionCheckService != null) {
            customPermissionCheckService.checkPermission(context);
        }
    }

    /**
     * 判断用户是否有访问该租户的临时权限
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 是否用户权限访问
     */
    public boolean isAllowedAccess(long userId, long tenantId) {
        if (userId == NOT_LOGIN) {
            return false;
        }
        String key = HZeroService.getRealName(HZeroService.Platform.NAME) + ":TEMPORARY_TENANT:" + userId + ":" + tenantId;
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String value = redisHelper.strGet(key);
        redisHelper.clearCurrentDatabase();
        return org.springframework.util.StringUtils.hasText(value);
    }

}
