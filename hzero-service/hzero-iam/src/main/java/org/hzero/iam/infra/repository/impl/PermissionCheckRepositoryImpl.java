package org.hzero.iam.infra.repository.impl;

import java.time.LocalDate;
import java.util.*;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.infra.constant.PermissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.iam.api.dto.MenuPermissionSetDTO;
import org.hzero.iam.domain.entity.PermissionCheck;
import org.hzero.iam.domain.repository.PermissionCheckRepository;
import org.hzero.iam.infra.mapper.PermissionCheckMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 缺失权限管理
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
@Component
public class PermissionCheckRepositoryImpl extends BaseRepositoryImpl<PermissionCheck>
                implements PermissionCheckRepository {

    @Autowired
    private PermissionCheckMapper mapper;

    @Override
    @ProcessLovValue
    public Page<PermissionCheck> selectPermissionCheck(PermissionCheck permissionCheck, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> mapper.selectPermissionCheck(permissionCheck));
    }

    @Override
    public PermissionCheck selectPermissionDetail(Long permissionCheckId) {
        return mapper.selectPermissionDetail(permissionCheckId);
    }

    @Override
    public Page<Long> listPermissionCheckId(LocalDate localDate, String checkState, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> mapper.selectPermissionCheckId(localDate, checkState));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteById(List<Long> permissionCheckIds) {
        mapper.batchDeleteById(permissionCheckIds);
    }

    @Override
    public List<PermissionCheck> selectMenuPermissionSet(MenuPermissionSetDTO menuPermissionSetDTO) {
        Set<String> codes = new HashSet<>(Arrays.asList(menuPermissionSetDTO.getPermissionCodes()));
        return mapper.selectMenuPermissionSet(codes);
    }

}
