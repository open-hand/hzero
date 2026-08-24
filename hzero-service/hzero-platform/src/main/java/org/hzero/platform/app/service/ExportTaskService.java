package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.ExportTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-platform
 */
public interface ExportTaskService {

    ExportTask insert(ExportTask exportTask);

    ExportTask updateByTaskCode(String taskCode, ExportTask newTask);

    void cancel(Long tenantId, String taskCode);

}
