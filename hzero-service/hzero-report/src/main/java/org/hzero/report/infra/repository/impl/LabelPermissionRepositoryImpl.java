package org.hzero.report.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.LabelPermission;
import org.hzero.report.domain.repository.LabelPermissionRepository;
import org.hzero.report.infra.mapper.LabelPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 标签权限 资源库实现
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
@Component
public class LabelPermissionRepositoryImpl extends BaseRepositoryImpl<LabelPermission> implements LabelPermissionRepository {

    @Autowired
    private LabelPermissionMapper labelPermissionMapper;

    @Override
    public Page<LabelPermission> pageLabelPermission(Long labelTemplateId, Long tenantId, boolean flag, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> labelPermissionMapper.selectLabelPermissions(labelTemplateId, tenantId, flag));
    }
}
