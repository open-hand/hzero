package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.ExportTask;
import org.hzero.platform.domain.repository.ExportTaskRepository;
import org.springframework.stereotype.Repository;

/**
 * @author XCXCXCXCX
 * @date 2019/8/6
 * @project hzero-platform
 */
@Repository
public class ExportTaskRepositoryImpl extends BaseRepositoryImpl<ExportTask> implements ExportTaskRepository {

    @Override
    public Page<ExportTask> getExportTaskByTenant(PageRequest pageRequest, Long tenantId, String taskCode, String taskName,String serviceName, String state) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                this.selectByCondition(
                        Condition
                                .builder(ExportTask.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(ExportTask.FIELD_TENANT_ID, tenantId, true)
                                        .andLike(ExportTask.FIELD_TASK_CODE, taskCode, true)
                                        .andLike(ExportTask.FIELD_TASK_NAME, taskName, true)
                                        .andLike(ExportTask.FIELD_SERVICE_NAME, serviceName, true)
                                        .andEqualTo(ExportTask.FIELD_STATE, state, true)
                                )
                                .build()
                ));
    }

    @Override
    public Page<ExportTask> getExportTaskByUser(PageRequest pageRequest, Long tenantId, String taskCode, String taskName, String serviceName, String state) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                this.selectByCondition(
                        Condition
                                .builder(ExportTask.class)
                                .andWhere(Sqls.custom()
                                        .andEqualTo(ExportTask.FIELD_TENANT_ID, tenantId, true)
                                        .andLike(ExportTask.FIELD_TASK_CODE, taskCode, true)
                                        .andLike(ExportTask.FIELD_TASK_NAME, taskName, true)
                                        .andLike(ExportTask.FIELD_SERVICE_NAME, serviceName, true)
                                        .andEqualTo(ExportTask.FIELD_STATE, state, true)
                                        .andEqualTo(ExportTask.FIELD_CREATED_BY, DetailsHelper.getUserDetails().getUserId(), false)
                                )
                                .build()
                ));
    }
}
