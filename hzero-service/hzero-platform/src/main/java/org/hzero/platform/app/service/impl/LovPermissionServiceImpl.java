package org.hzero.platform.app.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.platform.app.service.LovPermissionService;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.vo.LovUrlPermissionVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.mapper.LovPermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 值集访问权限服务实现。
 *
 * @author 25287
 */
@Service
public class LovPermissionServiceImpl implements LovPermissionService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LovPermissionServiceImpl.class);

    private static final String MEMBER_TYPE_USER = "user";
    private static final String MEMBER_TYPE_CLIENT = "client";
    private static final String DEFAULT_REQUEST_METHOD = "GET";
    private static final String ROUTE_MAP_KEY = HZeroService.Admin.CODE + ":routes";
    private static final int QUERY_BATCH_SIZE = 1000;

    private final LovPermissionMapper lovPermissionMapper;
    private final LovService lovService;
    private final RedisHelper redisHelper;
    private final Executor permissionQueryExecutor;
    private final Cache<PermissionCacheKey, Boolean> permissionCache;
    private final AtomicLong siteSuperAdminRoleId = new AtomicLong(-1L);
    private final AtomicLong tenantSuperAdminRoleId = new AtomicLong(-1L);

    @Autowired
    public LovPermissionServiceImpl(LovPermissionMapper lovPermissionMapper,
                                    LovService lovService,
                                    RedisHelper redisHelper,
                                    @Qualifier("commonAsyncTaskExecutor") Executor permissionQueryExecutor,
                                    @Value("${hzero.platform.lov-permission.local-cache.expire-seconds:60}")
                                    long cacheExpireSeconds,
                                    @Value("${hzero.platform.lov-permission.local-cache.maximum-size:50000}")
                                    long cacheMaximumSize) {
        this.lovPermissionMapper = lovPermissionMapper;
        this.lovService = lovService;
        this.redisHelper = redisHelper;
        this.permissionQueryExecutor = permissionQueryExecutor;
        this.permissionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheExpireSeconds, TimeUnit.SECONDS)
                .maximumSize(cacheMaximumSize)
                .build();
    }

    @Override
    public void afterPropertiesSet() {
        List<Long> tenantSuperRoleIds = lovPermissionMapper.selectRoleIdsByCode(HZeroConstant.RoleCode.TENANT);
        if (CollectionUtils.isNotEmpty(tenantSuperRoleIds)) {
            tenantSuperAdminRoleId.set(tenantSuperRoleIds.get(0));
        } else {
            LOGGER.warn("Tenant super admin role not found, roleCode={}", HZeroConstant.RoleCode.TENANT);
        }

        List<Long> siteSuperRoleIds = lovPermissionMapper.selectRoleIdsByCode(HZeroConstant.RoleCode.SITE);
        if (CollectionUtils.isNotEmpty(siteSuperRoleIds)) {
            siteSuperAdminRoleId.set(siteSuperRoleIds.get(0));
        } else {
            LOGGER.warn("Site super admin role not found, roleCode={}", HZeroConstant.RoleCode.SITE);
        }
    }

    @Override
    public boolean checkRole() {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails == null) {
            return false;
        }
        MemberPermissionContext memberContext = createMemberContext(userDetails);
        return memberContext != null && CollectionUtils.isNotEmpty(memberContext.getRoleIds());
    }

    @Override
    public boolean checkPermission(Collection<String> lovCodes) {
        List<String> distinctLovCodes = Optional.ofNullable(lovCodes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(distinctLovCodes)) {
            return false;
        }

        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails == null) {
            return false;
        }

        MemberPermissionContext memberContext = createMemberContext(userDetails);
        if (memberContext == null || CollectionUtils.isEmpty(memberContext.getRoleIds())) {
            return false;
        }

        Map<String, PermissionCacheKey> cacheKeys = createCacheKeys(memberContext, distinctLovCodes);
        Map<PermissionCacheKey, Boolean> cachedPermissions = permissionCache.getAllPresent(cacheKeys.values());
        List<String> uncachedLovCodes = new ArrayList<>();

        for (String lovCode : distinctLovCodes) {
            Boolean permitted = cachedPermissions.get(cacheKeys.get(lovCode));
            if (Boolean.FALSE.equals(permitted)) {
                return false;
            }
            if (permitted == null) {
                uncachedLovCodes.add(lovCode);
            }
        }

        if (uncachedLovCodes.isEmpty()) {
            return true;
        }

        Map<String, Lov> lovInfoMap = queryLovInfoMap(memberContext.getTenantId(), userDetails.getLanguage(),
                uncachedLovCodes);
        if (lovInfoMap.size() != uncachedLovCodes.size()) {
            return false;
        }

        if (hasSuperAdminRole(memberContext)) {
            cachePermissions(cacheKeys, uncachedLovCodes, Collections.emptySet(), true);
            return true;
        }

        Set<String> accessibleLovCodes = queryAccessibleLovCodes(memberContext, lovInfoMap.values());
        cachePermissions(cacheKeys, uncachedLovCodes, accessibleLovCodes, false);
        return accessibleLovCodes.containsAll(uncachedLovCodes);
    }

    private Map<String, PermissionCacheKey> createCacheKeys(MemberPermissionContext memberContext,
                                                             Collection<String> lovCodes) {
        Map<String, PermissionCacheKey> cacheKeys = new LinkedHashMap<>(lovCodes.size());
        for (String lovCode : lovCodes) {
            cacheKeys.put(lovCode, new PermissionCacheKey(memberContext, lovCode));
        }
        return cacheKeys;
    }

    private void cachePermissions(Map<String, PermissionCacheKey> cacheKeys, Collection<String> lovCodes,
                                  Set<String> accessibleLovCodes, boolean allAccessible) {
        for (String lovCode : lovCodes) {
            permissionCache.put(cacheKeys.get(lovCode), allAccessible || accessibleLovCodes.contains(lovCode));
        }
    }

    private Map<String, Lov> queryLovInfoMap(Long tenantId, String lang, Collection<String> lovCodes) {
        Map<String, CompletableFuture<Lov>> lovInfoFutures = new LinkedHashMap<>(lovCodes.size());
        for (String lovCode : lovCodes) {
            lovInfoFutures.put(lovCode, CompletableFuture.supplyAsync(
                    () -> lovService.queryLovInfo(lovCode, tenantId, lang), permissionQueryExecutor));
        }

        Map<String, Lov> lovInfoMap = new LinkedHashMap<>(lovCodes.size());
        lovInfoFutures.forEach((lovCode, future) -> {
            Lov lov = joinPermissionFuture(future);
            if (lov != null) {
                lovInfoMap.put(lovCode, lov);
            }
        });
        return lovInfoMap;
    }

    private Set<String> queryAccessibleLovCodes(MemberPermissionContext memberContext, Collection<Lov> lovInfoList) {
        Map<String, List<Lov>> lovsByType = lovInfoList.stream()
                .collect(Collectors.groupingBy(Lov::getLovTypeCode));

        List<CompletableFuture<Set<String>>> permissionFutures = new ArrayList<>(2);
        permissionFutures.add(CompletableFuture.supplyAsync(
                () -> checkSqlLovPermission(memberContext,
                        lovsByType.getOrDefault(FndConstants.LovTypeCode.SQL, Collections.emptyList())),
                permissionQueryExecutor));
        permissionFutures.add(CompletableFuture.supplyAsync(
                () -> checkUrlLovPermission(memberContext,
                        lovsByType.getOrDefault(FndConstants.LovTypeCode.URL, Collections.emptyList())),
                permissionQueryExecutor));

        Set<String> accessibleLovCodes = new HashSet<>();
        lovsByType.getOrDefault(FndConstants.LovTypeCode.INDEPENDENT, Collections.emptyList())
                .stream()
                .map(Lov::getLovCode)
                .forEach(accessibleLovCodes::add);

        for (CompletableFuture<Set<String>> permissionFuture : permissionFutures) {
            accessibleLovCodes.addAll(joinPermissionFuture(permissionFuture));
        }
        return accessibleLovCodes;
    }

    private Set<String> checkSqlLovPermission(MemberPermissionContext memberContext, List<Lov> sqlLovs) {
        Set<String> accessibleLovCodes = new HashSet<>();
        List<String> protectedLovCodes = new ArrayList<>();

        sqlLovs.forEach(lov -> {
            if (BaseConstants.Flag.YES.equals(lov.getPublicFlag())) {
                accessibleLovCodes.add(lov.getLovCode());
            } else {
                protectedLovCodes.add(lov.getLovCode());
            }
        });
        accessibleLovCodes.addAll(selectAccessiblePermissionCodes(memberContext, protectedLovCodes));
        return accessibleLovCodes;
    }

    private Set<String> checkUrlLovPermission(MemberPermissionContext memberContext, List<Lov> urlLovs) {
        if (CollectionUtils.isEmpty(urlLovs)) {
            return Collections.emptySet();
        }

        Map<String, String> serviceNames = new HashMap<>(16);
        List<LovUrlPermissionVO> urlPermissionParams = new ArrayList<>(urlLovs.size());
        for (Lov lov : urlLovs) {
            String serviceName = serviceNames.computeIfAbsent(lov.getRouteName(), this::getServiceName);
            String path = removeQueryString(lov.getCustomUrl());
            String method = DEFAULT_REQUEST_METHOD.toLowerCase(Locale.ROOT);
            if (StringUtils.isNotBlank(serviceName) && StringUtils.isNotBlank(path)) {
                urlPermissionParams.add(new LovUrlPermissionVO()
                        .setLovCode(lov.getLovCode())
                        .setServiceName(serviceName)
                        .setPath(path)
                        .setMethod(method));
            }
        }

        List<LovUrlPermissionVO> urlPermissions = selectUrlPermissions(urlPermissionParams);
        Set<String> accessibleLovCodes = new HashSet<>();
        Map<String, Set<String>> lovCodesByPermissionCode = new HashMap<>(urlPermissions.size());

        for (LovUrlPermissionVO urlPermission : urlPermissions) {
            if (Boolean.TRUE.equals(urlPermission.getPublicAccess())
                    || Boolean.TRUE.equals(urlPermission.getLoginAccess())) {
                accessibleLovCodes.add(urlPermission.getLovCode());
            } else if (StringUtils.isNotBlank(urlPermission.getPermissionCode())) {
                lovCodesByPermissionCode
                        .computeIfAbsent(urlPermission.getPermissionCode(), key -> new HashSet<>())
                        .add(urlPermission.getLovCode());
            }
        }

        if (!lovCodesByPermissionCode.isEmpty()) {
            Set<String> accessiblePermissionCodes = selectAccessiblePermissionCodes(memberContext,
                    lovCodesByPermissionCode.keySet());
            accessiblePermissionCodes.stream()
                    .map(lovCodesByPermissionCode::get)
                    .filter(Objects::nonNull)
                    .forEach(accessibleLovCodes::addAll);
        }
        return accessibleLovCodes;
    }

    private List<LovUrlPermissionVO> selectUrlPermissions(List<LovUrlPermissionVO> urlPermissionParams) {
        if (CollectionUtils.isEmpty(urlPermissionParams)) {
            return Collections.emptyList();
        }
        List<LovUrlPermissionVO> urlPermissions = new ArrayList<>();
        for (List<LovUrlPermissionVO> batchParams : ListUtils.partition(urlPermissionParams, QUERY_BATCH_SIZE)) {
            List<LovUrlPermissionVO> batchResult = lovPermissionMapper.selectUrlPermissions(batchParams);
            if (CollectionUtils.isNotEmpty(batchResult)) {
                urlPermissions.addAll(batchResult);
            }
        }
        return urlPermissions;
    }

    private String getServiceName(String routeName) {
        if (StringUtils.isBlank(routeName)) {
            return null;
        }
        return SafeRedisHelper.execute(HZeroService.Admin.REDIS_DB, redisHelper,
                () -> redisHelper.hshGet(ROUTE_MAP_KEY, routeName));
    }

    private String removeQueryString(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        int queryIndex = url.indexOf(BaseConstants.Symbol.QUESTION);
        return queryIndex < 0 ? url : url.substring(0, queryIndex);
    }

    private Set<String> selectAccessiblePermissionCodes(MemberPermissionContext memberContext,
                                                        Collection<String> permissionCodes) {
        if (CollectionUtils.isEmpty(permissionCodes)) {
            return Collections.emptySet();
        }

        Set<String> accessiblePermissionCodes = new HashSet<>();
        List<String> permissionCodeList = new ArrayList<>(permissionCodes);
        for (List<String> batchPermissionCodes : ListUtils.partition(permissionCodeList, QUERY_BATCH_SIZE)) {
            List<String> batchResult = lovPermissionMapper.selectAccessiblePermissionCodes(
                    memberContext.getMemberId(), memberContext.getMemberType(), memberContext.getRoleIds(),
                    batchPermissionCodes, memberContext.hasSecurityGroup(), true);
            if (CollectionUtils.isNotEmpty(batchResult)) {
                accessiblePermissionCodes.addAll(batchResult);
            }
        }
        return accessiblePermissionCodes;
    }

    private <T> T joinPermissionFuture(CompletableFuture<T> permissionFuture) {
        try {
            return permissionFuture.join();
        } catch (CompletionException ex) {
            throw unwrapPermissionException(ex);
        }
    }

    private RuntimeException unwrapPermissionException(Throwable exception) {
        Throwable cause = exception instanceof CompletionException && exception.getCause() != null
                ? exception.getCause() : exception;
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new RuntimeException(cause);
    }

    private boolean hasSuperAdminRole(MemberPermissionContext memberContext) {
        return memberContext.getRoleIds().contains(siteSuperAdminRoleId.get())
                || memberContext.getRoleIds().contains(tenantSuperAdminRoleId.get());
    }

    private MemberPermissionContext createMemberContext(CustomUserDetails userDetails) {
        if (userDetails.getClientId() != null) {
            return new MemberPermissionContext(userDetails.getClientId(), MEMBER_TYPE_CLIENT,
                    userDetails.getTenantId(), normalizeIds(userDetails.getRoleIds()));
        }
        if (userDetails.getUserId() != null) {
            return new MemberPermissionContext(userDetails.getUserId(), MEMBER_TYPE_USER,
                    userDetails.getTenantId(), normalizeIds(userDetails.roleMergeIds()));
        }
        return null;
    }

    private List<Long> normalizeIds(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        Set<Long> distinctIds = new TreeSet<>();
        for (Long id : ids) {
            if (id != null) {
                distinctIds.add(id);
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(distinctIds));
    }

    private static class MemberPermissionContext {
        private final Long memberId;
        private final String memberType;
        private final Long tenantId;
        private final List<Long> roleIds;

        private MemberPermissionContext(Long memberId, String memberType, Long tenantId, List<Long> roleIds) {
            this.memberId = memberId;
            this.memberType = memberType;
            this.tenantId = tenantId;
            this.roleIds = roleIds;
        }

        private Long getMemberId() {
            return memberId;
        }

        private String getMemberType() {
            return memberType;
        }

        private Long getTenantId() {
            return tenantId;
        }

        private List<Long> getRoleIds() {
            return roleIds;
        }

        private boolean hasSecurityGroup() {
            // 1.6 的用户上下文不包含安全组 ID，由查询本身判断当前角色是否有安全组授权。
            return true;
        }
    }

    private static class PermissionCacheKey {
        private final Long memberId;
        private final String memberType;
        private final Long tenantId;
        private final List<Long> roleIds;
        private final String lovCode;

        private PermissionCacheKey(MemberPermissionContext memberContext, String lovCode) {
            this.memberId = memberContext.memberId;
            this.memberType = memberContext.memberType;
            this.tenantId = memberContext.tenantId;
            this.roleIds = memberContext.roleIds;
            this.lovCode = lovCode;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            PermissionCacheKey that = (PermissionCacheKey) object;
            return Objects.equals(memberId, that.memberId)
                    && Objects.equals(memberType, that.memberType)
                    && Objects.equals(tenantId, that.tenantId)
                    && Objects.equals(roleIds, that.roleIds)
                    && Objects.equals(lovCode, that.lovCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(memberId, memberType, tenantId, roleIds, lovCode);
        }
    }
}
