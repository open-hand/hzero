package org.hzero.boot.report.app.impl;

import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.hzero.boot.file.FileClient;
import org.hzero.boot.report.app.ReportExecuteService;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.core.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 16:40
 */
@Component
public class ReportExecuteServiceImpl implements ReportExecuteService {

    private final FileClient fileClient;
    private final AsyncTaskExecutor taskExecutor;
    private final RedisQueueHelper redisQueueHelper;

    private static final List<String> EXCLUDE_PARAM = new ArrayList<>();

    static {
        EXCLUDE_PARAM.add("page");
        EXCLUDE_PARAM.add("size");
        EXCLUDE_PARAM.add("access_token");
    }

    @Autowired
    public ReportExecuteServiceImpl(FileClient fileClient,
                                    @Qualifier("report-client-executor") AsyncTaskExecutor taskExecutor,
                                    RedisQueueHelper redisQueueHelper) {
        this.fileClient = fileClient;
        this.taskExecutor = taskExecutor;
        this.redisQueueHelper = redisQueueHelper;
    }


    @Override
    public String execute(HttpServletRequest request) {
        Map<String, String> params = buildParams(request);
        String reportUuid = HZeroService.Report.CODE + UUIDUtils.generateUUID();
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        // 启动数据导入线程
        taskExecutor.execute(new ReportExecute(reportUuid, fileClient, params, redisQueueHelper, customUserDetails));
        return reportUuid;
    }

    /**
     * 构建参数
     */
    private Map<String, String> buildParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>(16);
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String name = String.valueOf(paraNames.nextElement());
            if (EXCLUDE_PARAM.contains(name)) {
                continue;
            }
            String value = request.getParameter(name);
            params.put(name, value);
        }
        return params;
    }
}
