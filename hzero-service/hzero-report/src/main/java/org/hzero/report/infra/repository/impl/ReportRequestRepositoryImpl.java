package org.hzero.report.infra.repository.impl;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.ReportRequest;
import org.hzero.report.domain.repository.ReportRequestRepository;
import org.hzero.report.infra.mapper.ReportRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表请求 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
@Component
public class ReportRequestRepositoryImpl extends BaseRepositoryImpl<ReportRequest> implements ReportRequestRepository {

    @Autowired
    private ReportRequestMapper reportRequestMapper;

    @Override
    public Page<ReportRequest> selectReportRequests(PageRequest pageRequest, ReportRequest reportRequest) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        Assert.notNull(customUserDetails, BaseConstants.ErrorCode.NOT_LOGIN);
        List<Long> roleIds = customUserDetails.getRoleIds();
        Long tenantId = customUserDetails.getTenantId();
        return PageHelper.doPageAndSort(pageRequest, () -> reportRequestMapper.selectReportRequests(roleIds, tenantId, reportRequest));
    }

    @Override
    public ReportRequest selectReportRequest(Long requestId) {
        return reportRequestMapper.selectReportRequest(requestId);
    }

}
