package org.hzero.boot.report.app.impl;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.hzero.boot.file.FileClient;
import org.hzero.boot.report.app.IReportService;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisQueueHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 报表执行线程
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 15:30
 */
public class ReportExecute implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportExecute.class);

    private final String reportUuid;
    private final FileClient fileClient;
    private final Map<String, String> params;
    private final RedisQueueHelper redisQueueHelper;
    private final CustomUserDetails customUserDetails;

    public ReportExecute(String reportUuid,
                         FileClient fileClient,
                         Map<String, String> params,
                         RedisQueueHelper redisQueueHelper,
                         CustomUserDetails customUserDetails) {
        this.params = params;
        this.fileClient = fileClient;
        this.reportUuid = reportUuid;
        this.redisQueueHelper = redisQueueHelper;
        this.customUserDetails = customUserDetails;
    }

    @Override
    public void run() {
        try {
            DetailsHelper.setCustomUserDetails(customUserDetails);
            IReportService reportService = ApplicationContextHelper.getContext().getBean(IReportService.class);
            ByteArrayOutputStream outputStream = reportService.execute(params);
            String url = fileClient.uploadFile(DetailsHelper.getUserDetails().getTenantId(), HZeroService.Report.BUCKET_NAME, null, reportService.filename(), outputStream.toByteArray());
            redisQueueHelper.push(reportUuid, url);
        } catch (Exception e) {
            LOGGER.warn("report exception:", e);
            redisQueueHelper.push(reportUuid, e.getMessage());
        }
    }
}
