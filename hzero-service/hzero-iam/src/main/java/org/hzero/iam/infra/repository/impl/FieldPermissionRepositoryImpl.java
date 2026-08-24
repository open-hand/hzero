package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.iam.domain.entity.Field;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.domain.repository.FieldRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.FieldPermissionMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 接口字段权限维护 资源库实现
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@Component
public class FieldPermissionRepositoryImpl extends BaseRepositoryImpl<FieldPermission> implements FieldPermissionRepository {
    private static final String FIELD_PERMISSION_PREFIX = "hiam:field-permission";
    private static final Logger logger = LoggerFactory.getLogger(FieldPermissionRepositoryImpl.class);

    private FieldPermissionMapper fieldPermissionMapper;
    private FieldRepository fieldRepository;
    private RedisHelper redisHelper;

    @Autowired
    public FieldPermissionRepositoryImpl(FieldPermissionMapper fieldPermissionMapper,
                                         FieldRepository fieldRepository,
                                         RedisHelper redisHelper) {
        this.fieldPermissionMapper = fieldPermissionMapper;
        this.fieldRepository = fieldRepository;
        this.redisHelper = redisHelper;
    }

    @Override
    public Page<FieldPermission> pagePermission(long tenantId, long permissionId, String permissionDimension, long dimensionValue, String fieldDescription, String permissionType, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> fieldPermissionMapper.listPermission(tenantId, permissionId, permissionDimension, dimensionValue, fieldDescription, permissionType));
    }

    @Override
    public List<FieldPermission> pageAll(PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> fieldPermissionMapper.listAll());
    }

    @Override
    public void storePermission(FieldPermission fieldPermission) {
        redisHelper.hshPut(getFieldPermissionKey(fieldPermission), fieldPermission.getFieldName(), fieldPermission.getRuleCache());
    }

    @Override
    public void storePermission(List<FieldPermission> fieldPermissionList) {
        Map<Long, Field> fieldMap = mapField(fieldPermissionList);
        fieldPermissionList.forEach(fieldPermission ->
                redisHelper.hshPut(getFieldPermissionKey(fieldPermission, fieldMap.get(fieldPermission.getFieldId())),
                        fieldMap.get(fieldPermission.getFieldId()).getFieldName(), fieldPermission.getRuleCache())
        );
    }

    private Map<Long, Field> mapField(List<FieldPermission> fieldPermissionList) {
        List<Long> fieldIds = fieldPermissionList.stream().map(FieldPermission::getFieldId).collect(Collectors.toList());
        Map<Long, Field> fieldMap = fieldRepository.listField(fieldIds)
                .stream()
                .collect(Collectors.toMap(Field::getFieldId, Function.identity()));
        if (fieldPermissionList.size() != fieldMap.size()) {
            logger.error("The number of fields queried is inconsistent with expectations, expected : [{}], actual : [{}]", fieldIds, fieldMap.keySet());
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        return fieldMap;
    }

    @Override
    public void removePermission(FieldPermission fieldPermission) {
        redisHelper.hshDelete(getFieldPermissionKey(fieldPermission), fieldPermission.getFieldName());
    }

    @Override
    public void removePermission(List<FieldPermission> fieldPermissionList) {
        Map<Long, Field> fieldMap = mapField(fieldPermissionList);
        fieldPermissionList.forEach(fieldPermission ->
                redisHelper.hshDelete(getFieldPermissionKey(fieldPermission, fieldMap.get(fieldPermission.getFieldId())), fieldMap.get(fieldPermission.getFieldId()).getFieldName())
        );
    }

    @Override
    public void removeSecGrpPermission(Long tenantId, Long userId, List<Long> fieldIds, String dimension) {
        if (tenantId == null || userId == null || CollectionUtils.isEmpty(fieldIds)
                || StringUtils.isEmpty(dimension)) {
            return;
        }
        //删除单关联权限,即DATA_SOURCE = "SEC_GRP"
        fieldPermissionMapper.deleteSecGrpPermission(tenantId, userId, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE, fieldIds, dimension);
        //删除多关联权限,即DATA_SOURCE = "DEFAULT,SEC_GRP"
        fieldPermissionMapper.updateSecGrpPermission(tenantId, userId, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE,
                Constants.SecGrpAssign.DEFAULT_DATA_SOURCE, fieldIds, dimension);
    }

    @Override
    public List<FieldPermission> listSecGrpPermission(Long tenantId, Long userId, List<Long> fieldIds, String dimension) {
        if (tenantId == null || userId == null || CollectionUtils.isEmpty(fieldIds)
                || StringUtils.isEmpty(dimension)) {
            return new ArrayList<>();
        }
        return fieldPermissionMapper.listSecGrpPermission(tenantId,userId,fieldIds,dimension,
                Arrays.asList(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE,
                        Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE));
    }

    private String getFieldPermissionKey(FieldPermission fieldPermission) {
        if (StringUtils.hasText(fieldPermission.getServiceName())
                && StringUtils.hasText(fieldPermission.getMethod())
                && StringUtils.hasText(fieldPermission.getPath())) {
            return getFieldPermissionKey(fieldPermission, fieldPermission.getServiceName(), fieldPermission.getMethod(), fieldPermission.getPath());
        }
        Field field = fieldRepository.queryField(fieldPermission.getFieldId());
        Assert.notNull(field, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        return getFieldPermissionKey(fieldPermission.setFieldName(field.getFieldName()), field);
    }

    private String getFieldPermissionKey(FieldPermission fieldPermission, Field field) {
        return getFieldPermissionKey(fieldPermission, field.getServiceName(), field.getMethod(), field.getPath());
    }

    private String getFieldPermissionKey(FieldPermission fieldPermission, String serviceName, String method, String path) {
        return String.format("%s:%s:%s:%s:%s:%d", FIELD_PERMISSION_PREFIX,
                serviceName,
                method,
                path,
                fieldPermission.getPermissionDimension().toLowerCase(),
                fieldPermission.getDimensionValue());
    }
}
