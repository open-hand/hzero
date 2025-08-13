package org.hzero.report.app.service.impl;

import java.util.Date;
import java.util.Map;

import org.hzero.core.base.BaseConstants;
import org.hzero.report.app.service.ReportRequestService;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.ReportRequest;
import org.hzero.report.domain.repository.ReportRequestRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

/**
 * 报表请求应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
@Service
public class ReportRequestServiceImpl implements ReportRequestService {

    @Autowired
    private ReportRequestRepository requestRepository;

    @Override
    public Long initReportRequest(Long tenantId, Report report, Map<String, Object> formParams) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setReportId(report.getReportId());
        reportRequest.setTenantId(tenantId);
        reportRequest.setRequestParam(JSON.toJSONString(formParams));
        reportRequest.setRequestStatus(HrptConstants.RequestStatus.STATUS_R);
        reportRequest.setStartDate(new Date());
        reportRequest.setObjectVersionNumber((long) BaseConstants.Digital.ONE);
        requestRepository.insertSelective(reportRequest);
        return reportRequest.getRequestId();
    }

    @Override
    public void finishReportRequest(Long requestId, String requestStatus, String fileUrl, String requestMessage) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setRequestId(requestId);
        reportRequest.setRequestStatus(requestStatus);
        reportRequest.setFileUrl(fileUrl);
        reportRequest.setRequestMessage(requestMessage);
        reportRequest.setEndDate(new Date());
        reportRequest.setObjectVersionNumber((long) BaseConstants.Digital.ONE);
        requestRepository.updateByPrimaryKeySelective(reportRequest);
    }

}
