package org.hzero.report.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.ReportPermission;
import org.hzero.report.domain.repository.ReportPermissionRepository;
import org.hzero.report.infra.mapper.ReportPermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表权限 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
@Component
public class ReportPermissionRepositoryImpl extends BaseRepositoryImpl<ReportPermission> implements ReportPermissionRepository {

    @Autowired
    private ReportPermissionMapper reportPermissionMapper;

    @Override
    public Page<ReportPermission> pageReportPermission(Long reportId, Long tenantId, boolean flag, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> reportPermissionMapper.selectReportPermissions(reportId, tenantId, flag));
    }

    @Override
    public int selectCountByUnique(Long tenantId, Long roleId, Long reportId) {
        return reportPermissionMapper.selectCountByUnique(tenantId, roleId, reportId);
    }

}
