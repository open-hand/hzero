package org.hzero.platform.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.async.AsyncTaskState;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.net.RequestHeaderCopyInterceptor;
import org.hzero.platform.domain.entity.ExportTask;
import org.hzero.platform.domain.repository.ExportTaskRepository;
import org.hzero.platform.app.service.ExportTaskService;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-platform
 */
@Service
public class ExportTaskServiceImpl implements ExportTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportTaskServiceImpl.class);

    private static final String HTTP_PREFIX = "http://";
    private static final String EXPORT_ENDPOINT_CONTEXT = "/actuator/async-export-endpoint";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";
    private static final String REMOVED = "REMOVED";
    private static final String DONE = "DONE";
    private static final String CANCELED = "CANCELED";

    @Autowired
    private ExportTaskRepository exportTaskRepository;
    private RestTemplate restTemplate = new RestTemplate();
    {
        restTemplate.setInterceptors(Collections.singletonList(new RequestHeaderCopyInterceptor()));
    }

    @Override
    public ExportTask insert(ExportTask exportTask) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Long userId = DetailsHelper.getUserDetails().getUserId();
        exportTask.setTenantId(tenantId == null ? BaseConstants.DEFAULT_TENANT_ID : tenantId);
        exportTask.setUserId(userId);
        exportTaskRepository.insert(exportTask);
        return exportTask;
    }

    @Override
    public ExportTask updateByTaskCode(String taskCode, ExportTask newTask) {
        List<ExportTask> targets = exportTaskRepository.select(ExportTask.FIELD_TASK_CODE, taskCode);
        if(targets == null || targets.isEmpty()){
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EXPORT_TASK_NOT_EXISTS, taskCode);
        }
        ExportTask target = targets.get(0);
        target.setState(newTask.getState());
        target.setDownloadUrl(newTask.getDownloadUrl());
        target.setEndDateTime(new Date());
        exportTaskRepository.updateOptional(target, ExportTask.FIELD_STATE, ExportTask.FIELD_DOWNLOAD_URL, ExportTask.FIELD_END_DATE_TIME);
        return target;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void cancel(Long tenantId, String taskCode) {
        ExportTask queryParam = new ExportTask();
        queryParam.setUserId(DetailsHelper.getUserDetails().getUserId());
        queryParam.setTaskCode(taskCode);
        if(tenantId != null){
            queryParam.setTenantId(tenantId);
        }
        List<ExportTask> targets = exportTaskRepository.select(queryParam);
        if(targets == null || targets.isEmpty()){
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EXPORT_TASK_NOT_EXISTS, taskCode);
        }
        ExportTask target = targets.get(0);
        target.setState(AsyncTaskState.CANCELLED.name());
        //更新hpfm_export_task表
        exportTaskRepository.updateOptional(target, ExportTask.FIELD_STATE);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity =
                    restTemplate.exchange(parseUrl(target.getHostName(), taskCode), HttpMethod.POST, null, String.class);
        }catch (RestClientException e){
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EXPORT_TASK_CANCEL_FAILED, e.getMessage());
        }
        if(FAILED.equals(responseEntity.getBody())){
            throw new CommonException(HpfmMsgCodeConstants.ERROR_EXPORT_TASK_CANCEL_FAILED, "cancel failed");
        }
        LOGGER.info("canceled task[uuid=" + taskCode + "] success, operation code is " + responseEntity.getBody());
    }

    private String parseUrl(String hostName, String uuid) {
        if(!hostName.startsWith(HTTP_PREFIX)){
            return HTTP_PREFIX + hostName + EXPORT_ENDPOINT_CONTEXT + "/" + uuid;
        }
        return hostName + EXPORT_ENDPOINT_CONTEXT + "/" + uuid;
    }

}
