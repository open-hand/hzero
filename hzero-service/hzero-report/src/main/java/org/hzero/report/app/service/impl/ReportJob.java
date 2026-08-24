package org.hzero.report.app.service.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.app.service.ReportRequestService;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.service.IReportGenerateService;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.oauth.DetailsHelper;

/**
 * 定时生成报表执行类
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/04 14:53
 */
@JobHandler("generateReport")
public class ReportJob implements IJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReportJob.class);

    private final IReportMetaService reportMetaService;
    private final IReportGenerateService reportGenerateService;
    private final ReportRequestService reportRequestService;
    private final ObjectMapper objectMapper;
    private final MessageClient messageClient;
    private final FileClient fileClient;
    private final ReportConfig reportConfig;

    @Autowired
    public ReportJob(IReportMetaService reportMetaService,
                     IReportGenerateService reportGenerateService,
                     ReportRequestService reportRequestService,
                     ObjectMapper objectMapper,
                     MessageClient messageClient,
                     FileClient fileClient,
                     ReportConfig reportConfig) {
        this.reportMetaService = reportMetaService;
        this.reportGenerateService = reportGenerateService;
        this.reportRequestService = reportRequestService;
        this.objectMapper = objectMapper;
        this.messageClient = messageClient;
        this.fileClient = fileClient;
        this.reportConfig = reportConfig;
    }

    @Override
    public ReturnT execute(Map<String, String> map, SchedulerTool tool) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        String fileUrl = null;
        String reportUuid = map.get(HrptConstants.Scheduler.REPORT_UUID);
        if (StringUtils.isBlank(reportUuid)) {
            tool.error("获取报表uuid失败！");
            return ReturnT.FAILURE;
        }
        Report report = reportMetaService.getReportByKey(reportUuid);
        String param = map.get(HrptConstants.Scheduler.FORM_PARAMS);
        Map<String, Object> formParams = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        try {
            formParams = objectMapper.readValue(param, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        tool.updateProgress(10, "报表开始生成");
        if (report == null) {
            tool.error("报表查询失败！");
            return ReturnT.FAILURE;
        }
        if (Objects.equals(report.getReportTypeCode(), ReportTypeEnum.TABLE.getValue()) || Objects.equals(report.getReportTypeCode(), ReportTypeEnum.SIMPLE_TABLE.getValue())) {
            Long requestId = reportRequestService.initReportRequest(tenantId, report, formParams);
            if (requestId == null) {
                tool.error("初始化报表请求失败！");
                return ReturnT.FAILURE;
            }
            tool.debug("初始化报表请求成功");
            tool.updateProgress(30, "正在生成报表...");
            fileUrl = reportGenerateService.generateTable(tenantId, requestId, report, formParams);
            tool.debug("报表生成完成");
            tool.updateProgress(100, "报表生成完成...");
        } else {
            tool.warn("非表格报表，不能生成！");
        }
        if (!map.containsKey(HrptConstants.Scheduler.END_EMAIL)) {
            // 不需要发送邮件，执行结束
            return ReturnT.SUCCESS;
        }
        if (StringUtils.isNotBlank(fileUrl)) {
            // 发成功消息
            String signUrl = fileClient.getSignedUrl(tenantId, HZeroService.Report.BUCKET_NAME, null, fileUrl, reportConfig.getEmailFileExpires());
            Map<String, String> emailParam = new HashMap<>(16);
            emailParam.put("reportName", report.getReportName());
            emailParam.put("reportUrl", signUrl);
            sendEmail(tenantId, map.get(HrptConstants.Scheduler.END_EMAIL), HrptConstants.Scheduler.SUCCESS_TEMPLATE_CODE, emailParam);
        } else {
            // 发失败消息
            Map<String, String> emailParam = new HashMap<>(16);
            emailParam.put("reportName", report.getReportName());
            sendEmail(tenantId, map.get(HrptConstants.Scheduler.END_EMAIL), HrptConstants.Scheduler.FAILED_TEMPLATE_CODE, emailParam);
        }
        return ReturnT.SUCCESS;
    }

    private void sendEmail(Long tenantId, String email, String templateCode, Map<String, String> param) {
        Receiver receiver = new Receiver().setEmail(email);
        messageClient.async().sendMessage(tenantId, templateCode, Collections.singletonList(receiver), param);
    }
}
