package org.hzero.boot.report.app;

import javax.servlet.http.HttpServletRequest;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 16:39
 */
public interface ReportExecuteService {

    /**
     * 报表执行
     *
     * @param request 请求
     * @return 批次
     */
    String execute(HttpServletRequest request);
}
