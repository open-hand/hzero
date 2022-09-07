package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.ExportTask;

/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-platform
 */
public interface ExportTaskRepository extends BaseRepository<ExportTask> {

    /**
     * 获取导出任务（租户级）
     *
     * @param pageRequest
     * @param tenantId
     * @param taskCode
     * @param serviceName
     * @param state
     * @return
     */
    Page<ExportTask> getExportTaskByTenant(PageRequest pageRequest, Long tenantId, String taskCode, String taskName, String serviceName, String state);

    /**
     * 获取导出任务（用户级）
     *
     * @param pageRequest
     * @param tenantId
     * @param taskCode
     * @param serviceName
     * @param state
     * @return
     */
    Page<ExportTask> getExportTaskByUser(PageRequest pageRequest, Long tenantId, String taskCode, String taskName, String serviceName, String state);

}