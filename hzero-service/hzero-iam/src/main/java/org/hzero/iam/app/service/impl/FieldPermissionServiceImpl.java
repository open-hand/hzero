package org.hzero.iam.app.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.FieldPermissionService;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.domain.service.role.AbstractAuthorityCommonService;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 接口字段权限维护应用服务默认实现
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@Service
public class FieldPermissionServiceImpl extends AbstractAuthorityCommonService implements FieldPermissionService {
    private static final Logger LOG = LoggerFactory.getLogger(FieldPermissionServiceImpl.class);
    private static final int REFRESH_BATCH_SIZE = 400;
    private static final int REFRESH_PAGE_START = 0;
    private final FieldPermissionRepository fieldPermissionRepository;

    @Autowired
    public FieldPermissionServiceImpl(FieldPermissionRepository fieldPermissionRepository) {
        this.fieldPermissionRepository = fieldPermissionRepository;
    }

    @Override
    public Page<FieldPermission> pagePermission(long tenantId, long permissionId, String permissionDimension, long dimensionValue, String fieldDescription, String permissionType, PageRequest pageRequest) {
        return fieldPermissionRepository.pagePermission(tenantId, permissionId, permissionDimension, dimensionValue, fieldDescription, permissionType, pageRequest);
    }

    @Override
    public List<FieldPermission> listFieldPermission(Long fieldId) {
        return fieldPermissionRepository.selectByCondition(Condition.builder(FieldPermission.class)
                .select(FieldPermission.FIELD_FIELD_PERMISSION_ID, FieldPermission.FIELD_FIELD_ID, FieldPermission.FIELD_PERMISSION_DIMENSION, FieldPermission.FIELD_DIMENSION_VALUE)
                .andWhere(Sqls.custom().andEqualTo(FieldPermission.FIELD_FIELD_ID, fieldId))
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FieldPermission createPermission(FieldPermission fieldPermission) {
        FieldPermission fieldPermissionParam = new FieldPermission();
        fieldPermissionParam.setDimensionValue(fieldPermission.getDimensionValue());
        fieldPermissionParam.setPermissionDimension(fieldPermission.getPermissionDimension());
        fieldPermissionParam.setFieldId(fieldPermission.getFieldId());
        fieldPermissionParam.setTenantId(fieldPermission.getTenantId());
        FieldPermission hasFieldPermission = fieldPermissionRepository.selectOne(fieldPermissionParam);
        if(hasFieldPermission != null) {
            if(containSecGrpDataSource(hasFieldPermission.getDataSource())) {
                hasFieldPermission.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                fieldPermissionRepository.updateByPrimaryKeySelective(hasFieldPermission);
                fieldPermission.setFieldPermissionId(hasFieldPermission.getFieldPermissionId());
            } else {
                throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
            }
        } else {
            fieldPermissionRepository.insertSelective(fieldPermission);
            fieldPermissionRepository.storePermission(fieldPermission);
        }
        return fieldPermission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FieldPermission> createPermission(List<FieldPermission> fieldPermissionList) {
        if (CollectionUtils.isEmpty(fieldPermissionList)) {
            return fieldPermissionList;
        }
        for(FieldPermission fieldPermission : fieldPermissionList) {
            createPermission(fieldPermission);
        }
//        fieldPermissionList.forEach(fieldPermissionRepository::insertSelective);
//        fieldPermissionRepository.storePermission(fieldPermissionList);
        return fieldPermissionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FieldPermission updatePermission(FieldPermission fieldPermission) {
        fieldPermissionRepository.updateOptional(fieldPermission, FieldPermission.getUpdatableField());
        fieldPermissionRepository.storePermission(fieldPermission);
        return fieldPermission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FieldPermission> updatePermission(List<FieldPermission> fieldPermissionList) {
        if (CollectionUtils.isEmpty(fieldPermissionList)) {
            return fieldPermissionList;
        }
        fieldPermissionList.forEach(fieldPermission -> fieldPermissionRepository.updateOptional(fieldPermission, FieldPermission.getUpdatableField()));
        fieldPermissionRepository.storePermission(fieldPermissionList);
        return fieldPermissionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(FieldPermission fieldPermission) {
        FieldPermission hasFieldPermission = fieldPermissionRepository.selectByPrimaryKey(fieldPermission.getFieldPermissionId());

        if (Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE.equals(hasFieldPermission.getDataSource())) {
            // 子账户和角色管理下使用，具有默认权限和安全组分配的权限
            hasFieldPermission.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
            fieldPermissionRepository.updateByPrimaryKeySelective(hasFieldPermission);
        } else {
            fieldPermissionRepository.deleteByPrimaryKey(fieldPermission);
            fieldPermissionRepository.removePermission(fieldPermission);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(List<FieldPermission> fieldPermissionList) {
        if (CollectionUtils.isEmpty(fieldPermissionList)) {
            return;
        }
        for(FieldPermission fieldPermission : fieldPermissionList) {
            deletePermission(fieldPermission);
        }
//        fieldPermissionList.forEach(fieldPermissionRepository::deleteByPrimaryKey);
//        fieldPermissionRepository.removePermission(fieldPermissionList);
    }

    @Override
    public void restorePermission() {
        List<FieldPermission> fieldPermissionList = null;
        PageRequest pageRequest = new PageRequest(REFRESH_PAGE_START, REFRESH_BATCH_SIZE, new Sort(FieldPermission.FIELD_FIELD_PERMISSION_ID));
        while (fieldPermissionList == null || !fieldPermissionList.isEmpty()) {
            fieldPermissionList = fieldPermissionRepository.pageAll(pageRequest);
            fieldPermissionList.forEach(item -> fieldPermissionRepository.storePermission(item));
            pageRequest.setPage(pageRequest.getPage() + 1);
        }
    }
}
