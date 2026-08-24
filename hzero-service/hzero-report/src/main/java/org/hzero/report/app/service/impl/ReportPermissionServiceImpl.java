package org.hzero.report.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.report.app.service.ReportPermissionService;
import org.hzero.report.domain.entity.ReportPermission;
import org.hzero.report.domain.repository.ReportPermissionRepository;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * 报表权限应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
@Service
public class ReportPermissionServiceImpl implements ReportPermissionService {

    @Autowired
    private ReportPermissionRepository reportPermissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReportPermission(ReportPermission reportPermission) {
        // 验证重复
        validatePermissionDtlRepeat(reportPermission);
        // 插入数据
        reportPermissionRepository.insertSelective(reportPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportPermission updateReportPermission(ReportPermission reportPermission) {
        reportPermissionRepository.updateOptional(reportPermission, ReportPermission.FIELD_START_DATE,
                ReportPermission.FIELD_END_DATE, ReportPermission.FIELD_REMARK);
        return reportPermission;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeReportPermission(Long reportPermissionId) {
        reportPermissionRepository.deleteByPrimaryKey(reportPermissionId);
    }

    /**
     * 校验报表权限重复性
     */
    private void validatePermissionDtlRepeat(ReportPermission reportPermission) {
        int cnt = reportPermissionRepository.selectCountByUnique(reportPermission.getTenantId(),
                reportPermission.getRoleId(), reportPermission.getReportId());
        if (cnt > BaseConstants.Digital.ZERO) {
            throw new CommonException(HrptMessageConstants.ERROR_CODE_REPEAT);
        }
    }

}
