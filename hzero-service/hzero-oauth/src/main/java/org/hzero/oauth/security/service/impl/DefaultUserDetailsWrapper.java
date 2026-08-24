package org.hzero.oauth.security.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.helper.LanguageHelper;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.domian.Language;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.domain.service.RootUserService;
import org.hzero.oauth.domain.vo.Role;
import org.hzero.oauth.domain.vo.UserRoleDetails;
import org.hzero.oauth.domain.vo.UserVO;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.service.UserDetailsWrapper;

/**
 * 处理 UserDetails
 *
 * @author bojiangzhou 2019/02/27
 */
public class DefaultUserDetailsWrapper implements UserDetailsWrapper {
    private static final Logger logger = LoggerFactory.getLogger(DefaultUserDetailsWrapper.class);
    private static final String ROLE_MERGE_PREFIX = "hpfm:config:ROLE_MERGE.";
    private static final String TENANT_DEFAULT_LANGUAGE = "TENANT_DEFAULT_LANGUAGE";

    private UserRepository userRepository;
    private RedisHelper redisHelper;

    public DefaultUserDetailsWrapper(UserRepository userRepository, RedisHelper redisHelper) {
        this.userRepository = userRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public void warp(CustomUserDetails details, Long userId, Long tenantId, boolean login) {
        logger.debug(">>>>> Before warp[{},{}] : {}", userId, tenantId, details);
        if (details.getTenantId() != null) {
            tenantId = details.getTenantId();
        }
        List<UserRoleDetails> roleDetailList = selectUserRoles(details, tenantId);
        if (CollectionUtils.isNotEmpty(roleDetailList)) {
            List<Long> tenantIds = roleDetailList.stream().map(UserRoleDetails::getTenantId).distinct().collect(Collectors.toList());
            // 如果是登录
            if (login) {
                UserRoleDetails userRoleDetails = roleDetailList.get(0);
                // 如果有设置默认租户并且默认租户在可访问租户列表中取默认租户
                if (userRoleDetails.getDefaultTenantId() != null && tenantIds.contains(userRoleDetails.getDefaultTenantId())) {
                    tenantId = userRoleDetails.getDefaultTenantId();
                }
                // 如果没有默认租户，有租户访问历史并且最近访问租户再可访问租户列表中，默认登录最近访问租户
                else if (userRoleDetails.getAccessDatetime() != null && tenantIds.contains(userRoleDetails.getTenantId())) {
                    tenantId = userRoleDetails.getTenantId();
                }
            }
            // 如果当前租户不属于可访问租户列表，取可访问租户列表第一条
            if (!tenantIds.contains(tenantId)) {
                tenantId = tenantIds.stream()
                        .findFirst()
                        .orElseThrow(() -> new CommonException(LoginExceptions.ROLE_NONE.value()));
            }
            for (UserRoleDetails roleDetails : roleDetailList) {
                if (Objects.equals(tenantId, roleDetails.getTenantId())) {
                    // 筛选当前租户下可访问的角色（出现冲突时必定是数据问题，这里留一手）
                    Map<Long, Role> roleMap = roleDetails.getRoles().stream().collect(Collectors.toMap(Role::getId, Function.identity(), (v1, v2) -> v1));
                    // 防止加载用户信息时覆盖掉当前用户选择的租户
                    if (details.getRoleId() == null || !roleMap.containsKey(details.getRoleId())) {
                        Role role;
                        if (roleMap.containsKey(roleDetails.getDefaultRoleId())) {
                            role = roleMap.get(roleDetails.getDefaultRoleId());
                        } else {
                            role = roleDetails.getRoles().stream().findFirst().orElse(new Role());
                        }
                        details.setRoleId(role.getId());
                    }
                    details.setRoleIds(new ArrayList<>(roleMap.keySet()));
                    details.setSiteRoleIds(roleMap.values().stream()
                            .filter(item -> "site".equals(item.getLevel())).map(Role::getId)
                            .collect(Collectors.toList()));
                    details.setTenantRoleIds(roleMap.values().stream()
                            .filter(item -> "organization".equals(item.getLevel())).map(Role::getId)
                            .collect(Collectors.toList()));
                    details.setTenantIds(tenantIds);
                    if (details.getTenantId() != null && !tenantIds.contains(details.getTenantId())) {
                        details.setTenantId(null);
                    } else {
                        details.setTenantId(tenantId);
                        details.setTenantNum(roleDetails.getTenantNum());
                        this.initRoleMergeFlag(details, roleDetails);
                        this.initUserLanguage(details, roleDetails);
                    }
                    break;
                }
            }
        }

        if (CollectionUtils.isEmpty(details.getRoleIds())) {
            logger.warn("User not assign any role! userId: {}", details.getUserId());
        }

        warpRoleInfo(details, details.getRoleId());

        logger.debug(">>>>> After warp[{},{}] : {}", userId, tenantId, details);
    }

    @Override
    public void warpRoleInfo(CustomUserDetails details, Long roleId) {
        details.setRoleId(roleId);
        // 设置角色标签
        Set<String> labels = Optional.ofNullable(userRepository.selectRoleLabels(details.roleMergeIds())).orElse(new HashSet<>(0));
        details.setRoleLabels(labels);
    }

    protected List<UserRoleDetails> selectUserRoles(CustomUserDetails details, Long tenantId) {
        List<UserRoleDetails> roleDetailList = userRepository.selectRoleDetails(details.getUserId());

        if (RootUserService.isRootUser(details)) {
            // 查询出 root 用户可访问的其它租户
            userRepository.selectRootUserRoleDetails(details.getUserId(), tenantId)
                    .stream()
                    .findFirst()
                    .ifPresent((tenant) -> {
                        UserRoleDetails userRoleDetails = roleDetailList.stream().filter(item -> Objects.equals(item.getTenantId(), tenantId)).findFirst().orElse(null);
                        if (userRoleDetails == null) {
                            roleDetailList.add(tenant);
                        } else {
                            Set<Long> ids = userRoleDetails.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
                            List<Role> adminRoles = tenant.getRoles().stream().filter(r -> !ids.contains(r.getId())).collect(Collectors.toList());
                            userRoleDetails.getRoles().addAll(adminRoles);
                        }
                    });
        }

        return roleDetailList;
    }

    /**
     * 初始化角色合并标识
     *
     * @param details     用户详情对象
     * @param roleDetails 角色详情
     */
    protected void initRoleMergeFlag(CustomUserDetails details, UserRoleDetails roleDetails) {
        details.setRoleMergeFlag(Optional.ofNullable(roleDetails.getRoleMergeFlag())
                .orElseGet(() -> {
                    String roleMergeFlag = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, () -> {
                        String str = this.redisHelper.strGet(ROLE_MERGE_PREFIX + details.getTenantId());
                        if (StringUtils.isBlank(str)) {
                            str = this.redisHelper.strGet(ROLE_MERGE_PREFIX + BaseConstants.DEFAULT_TENANT_ID.toString());
                        }
                        return str;
                    });

                    return StringUtils.isBlank(roleMergeFlag) ? Boolean.FALSE : Boolean.valueOf(roleMergeFlag);
                }));
    }

    /**
     * 初始化用户语言
     *
     * @param details     用户详情
     * @param roleDetails 角色详情
     */
    protected void initUserLanguage(CustomUserDetails details, UserRoleDetails roleDetails) {
        // 设置语言
        details.setLanguage(this.getUserInitLanguage(details, roleDetails));
    }

    /**
     * 获取用户初始化语言
     *
     * @param details     用户详情
     * @param roleDetails 角色详情
     * @return 用户初始化语言
     */
    private String getUserInitLanguage(CustomUserDetails details, UserRoleDetails roleDetails) {
        Long tenantId = details.getTenantId();

        String language;
        // 1、取用户界面选择的语言
        language = this.getUserPageLanguage();
        if (StringUtils.isNotBlank(language)) {
            return language;
        }

        // 2、取用户全局默认语言配置 (iam_user [language])
        language = roleDetails.getUserLanguage();
        if (StringUtils.isNotBlank(language)) {
            return language;
        }

        // 3、取租户默认语言
        language = this.getTenantDefaultLanguage(tenantId);
        if (StringUtils.isNotBlank(language)) {
            return language;
        }

        // 4、取系统默认语言
        return LanguageHelper.getDefaultLanguage();
    }

    /**
     * 取用户界面选择的语言
     *
     * @return 用户界面选择的语言
     */
    @Nullable
    private String getUserPageLanguage() {
        // 从session中获取数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        }

        Object initLangFlagAttribute = requestAttributes.getAttribute(SecurityAttributes.FIELD_INIT_LANG_FLAG,
                RequestAttributes.SCOPE_SESSION);
        // 如果初始化标识为 非false ，就说明是初始化操作，此时不从session中取语言
        if (initLangFlagAttribute instanceof Boolean && BooleanUtils.isNotFalse((Boolean) initLangFlagAttribute)) {
            return null;
        }

        Object attribute = requestAttributes.getAttribute(SecurityAttributes.FIELD_LANG, RequestAttributes.SCOPE_SESSION);
        if (Objects.isNull(attribute)) {
            return null;
        }

        if (attribute instanceof String) {
            return (String) attribute;
        } else if (attribute instanceof Language) {
            return ((Language) attribute).getCode();
        } else if (attribute instanceof org.hzero.oauth.domain.entity.Language) {
            return ((org.hzero.oauth.domain.entity.Language) attribute).getCode();
        }

        return null;
    }

    /**
     * 获取租户默认语言
     * <p>
     * 如果租户默认语言为空，就获取0租户的默认语言
     *
     * @param tenantId 租户ID
     * @return 租户默认语言
     */
    @Nullable
    private String getTenantDefaultLanguage(@NonNull Long tenantId) {
        return SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, () -> {
            // 3.1、当前租户默认配置
            String lang = this.redisHelper.strGet(UserVO.generateCacheKey(TENANT_DEFAULT_LANGUAGE, tenantId));
            if (StringUtils.isBlank(lang) && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
                // 3.2、取0租户默认配置
                lang = this.redisHelper.strGet(UserVO.generateCacheKey(TENANT_DEFAULT_LANGUAGE, BaseConstants.DEFAULT_TENANT_ID));
            }

            return lang;
        });
    }
}
