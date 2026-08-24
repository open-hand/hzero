package org.hzero.report.app.service;

import java.util.Map;

import org.hzero.report.domain.entity.Report;

/**
 * 报表请求应用服务
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
public interface ReportRequestService {

    /**
     * 初始化报表请求
     *
     * @param tenantId   租户Id
     * @param report     报表信息
     * @param formParams 参数
     * @return 请求主键ID
     */
    Long initReportRequest(Long tenantId, Report report, Map<String, Object> formParams);

    /**
     * 完成报表请求
     *
     * @param requestId      请求ID
     * @param requestStatus  请求状态
     * @param fileUrl        导出文件路径
     * @param requestMessage 请求消息
     */
    void finishReportRequest(Long requestId, String requestStatus, String fileUrl, String requestMessage);

}
