package org.hzero.report.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.report.app.service.LabelPermissionService;
import org.hzero.report.domain.entity.LabelPermission;
import org.hzero.report.domain.repository.LabelPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 标签权限应用服务默认实现
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
@Service
public class LabelPermissionServiceImpl implements LabelPermissionService {

    @Autowired
    private LabelPermissionRepository labelPermissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createLabelPermission(LabelPermission labelPermission) {
        Assert.isTrue(UniqueHelper.valid(labelPermission), BaseConstants.ErrorCode.DATA_EXISTS);
        labelPermissionRepository.insertSelective(labelPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LabelPermission updateLabelPermission(LabelPermission labelPermission) {
        Assert.isTrue(UniqueHelper.valid(labelPermission), BaseConstants.ErrorCode.DATA_EXISTS);
        labelPermissionRepository.updateOptional(labelPermission,
                LabelPermission.FIELD_START_DATE,
                LabelPermission.FIELD_END_DATE,
                LabelPermission.FIELD_REMARK);
        return labelPermission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeLabelPermission(Long permissionId) {
        labelPermissionRepository.deleteByPrimaryKey(permissionId);
    }
}
