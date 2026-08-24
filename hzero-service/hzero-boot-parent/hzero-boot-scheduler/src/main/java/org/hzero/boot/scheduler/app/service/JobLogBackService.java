package org.hzero.boot.scheduler.app.service;

import org.hzero.boot.scheduler.api.dto.JobLogDTO;

/**
 * 日志应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/24 10:10
 */
public interface JobLogBackService {

    /**
     * 回写日志
     *
     * @param jobLogDTO 日志
     */
    void updateLog(JobLogDTO jobLogDTO);
}
