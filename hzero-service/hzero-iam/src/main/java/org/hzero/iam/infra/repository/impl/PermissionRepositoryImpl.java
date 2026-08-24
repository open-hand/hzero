package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.repository.LabelRelRepository;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.domain.vo.PermissionCacheVO;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.iam.infra.mapper.PermissionMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限管理
 *
 * @author allen 2018/6/25
 */
@Repository
public class PermissionRepositoryImpl extends BaseRepositoryImpl<Permission> implements PermissionRepository {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private LabelRelRepository labelRelRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionRepositoryImpl.class);

    @Override
    public Page<PermissionVO> pagePermission(String condition, String level, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.selectPermissions(condition, level));
    }

    @Override
    public List<Permission> selectByCodes(String[] codes) {
        return selectByCondition(Condition.builder(Permission.class)
                .andWhere(Sqls.custom().andIn(Permission.FIELD_CODE, Arrays.asList(codes)))
                .build()
        );
    }

    @Override
    public List<Permission> selectByIds(List<Long> ids) {
        return selectByCondition(Condition.builder(Permission.class)
                .andWhere(Sqls.custom().andIn(Permission.FIELD_ID, ids))
                .build()
        );
    }

    @Override
    public List<Lov> selectLovByCodes(String[] codes, Long tenantId) {
        if (ArrayUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }
        return permissionMapper.selectLovByCodes(Arrays.asList(codes), tenantId);
    }

    @Override
    public void cacheServicePermissions(String serviceName, boolean clearCache) {
        serviceName = StringUtils.lowerCase(serviceName);
        List<Permission> permissions = select(Permission.FIELD_SERVICE_NAME, serviceName);
        LOGGER.info("cache service permissions, serviceName={}, permissionsSize={}", serviceName, permissions.size());
        // 处理权限的标签
        this.processPermissionLabel(permissions);

        String finalServiceName = serviceName;

        Map<String, Map<String, String>> map = permissions.parallelStream().collect(
                Collectors.groupingBy(Permission::getMethod,
                        Collectors.toMap(p -> String.valueOf(p.getId()), p -> redisHelper.toJson(new PermissionCacheVO(p)))
                )
        );

        SafeRedisHelper.execute(HZeroService.Gateway.REDIS_DB, () -> {
            if (clearCache) {
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.GET.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.POST.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.PUT.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.DELETE.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.PATCH.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.HEAD.name()));
                redisHelper.delKey(Permission.generateKey(finalServiceName, HttpMethod.OPTIONS.name()));
            }

            map.forEach((method, list) -> redisHelper.hshPutAll(Permission.generateKey(finalServiceName, method), list));
        });
    }

    @Override
    public List<Permission> selectSimpleByService(String serviceName) {
        return permissionMapper.selectSimpleByService(serviceName);
    }

    @Override
    public PermissionVO queryPermissionByCode(String permissionCode, String level) {
        return permissionMapper.queryPermissionByCode(permissionCode, level);
    }

    @Override
    @ProcessLovValue
    public Page<PermissionVO> pageTenantApis(PermissionVO permissionVO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.selectTenantApis(permissionVO));
    }

    @Override
    @ProcessLovValue
    public Page<PermissionVO> pageApis(PermissionVO permissionVO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.selectApis(permissionVO));
    }

    @Override
    @ProcessLovValue
    public Page<PermissionVO> pageTenantAssignableApis(PermissionVO permissionVO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.selectTenantAssignableApis(permissionVO));
    }

    @Override
    public List<Permission> listByServiceName(String serviceName) {
        if (StringUtils.isBlank(serviceName)) {
            return Collections.emptyList();
        }

        return this.selectByCondition(Condition.builder(Permission.class)
                .where(Sqls.custom()
                        .andEqualTo(Permission.FIELD_SERVICE_NAME, serviceName)
                ).build());
    }

    /**
     * 处理权限标签
     *
     * @param permissions 权限对象
     */
    private void processPermissionLabel(List<Permission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }

        // 获取所有权限数据ID
        Set<Long> permissionIds = permissions.parallelStream().map(Permission::getId).collect(Collectors.toSet());
        // 查询标签关联信息
        Map<Long, List<Label>> permissionLabels = this.labelRelRepository
                .selectLabelsByDataTypeAndDataIds(Permission.LABEL_DATA_TYPE, permissionIds);

        // 处理权限标签
        permissions.parallelStream()
                // 只处理包含标签的权限数据
                .filter(permission -> permissionLabels.containsKey(permission.getId()))
                .forEach(permission -> {
                    // 获取权限的标签
                    List<Label> labels = permissionLabels.get(permission.getId());

                    // 合并标签到tag字段
                    permission.setTag(this.resolveTag(labels));
                });
    }

    /**
     * 解析tag
     *
     * @param labels 权限对象的tag
     * @return 解析出来的tag
     */
    private String resolveTag(List<Label> labels) {
        return labels.stream().map(Label::getName).collect(Collectors.joining(BaseConstants.Symbol.COMMA));
    }
}
