package org.hzero.boot.platform.export;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.boot.file.FileClient;
import org.hzero.boot.file.constant.FileType;
import org.hzero.boot.platform.export.feign.ExportTaskService;
import org.hzero.common.HZeroService;
import org.hzero.core.async.AsyncTaskState;
import org.hzero.core.export.ExportAsyncTemplate;
import org.hzero.core.export.ExportTaskDTO;


/**
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-starter-parent
 */
public class DefaultExportAsyncTemplate implements ExportAsyncTemplate {

    public static final String BUCKET_NAME = HZeroService.Platform.BUCKET_NAME;
    public static final String DIRECTORY = HZeroService.Platform.CODE + "01";

    private static final String ZIP_SUFFIX = ".zip";
    private static final String EXCEL_SUFFIX = ".xlsx";

    @Autowired
    private ExportTaskService exportTaskService;
    @Autowired
    private FileClient fileClient;

    @Override
    public void afterSubmit(ExportTaskDTO dto) {
        //插入hpfm_export_task表
        dto.setState(AsyncTaskState.DOING);
        exportTaskService.insert(dto);
    }

    @Override
    public Object doWhenFinish(ExportTaskDTO dto, Map<String, Object> additionInfo) {

        String fileType = (String)additionInfo.get(ExportAsyncTemplate.FILE_TYPE_KEY);
        byte[] file = (byte[]) additionInfo.get(ExportAsyncTemplate.FILE_KEY);

        String fileSuffix = ExportAsyncTemplate.FILE_TYPE_EXCEL.equals(fileType) ? EXCEL_SUFFIX : ZIP_SUFFIX;
        String fileTypeParam = ExportAsyncTemplate.FILE_TYPE_EXCEL.equals(fileType) ? FileType.Application.XLS : FileType.Application.ZIP;

        //上传至hzero-file
        String downloadUrl = fileClient.uploadFile(dto.getTenantId(),
                BUCKET_NAME, DIRECTORY,
                dto.getTaskName() + fileSuffix,
                fileTypeParam, file);

        //更新hpfm_export_task表
        dto.setState(AsyncTaskState.DONE);
        dto.setDownloadUrl(downloadUrl);
        dto.setEndDateTime(new Date());
        exportTaskService.update(dto);

        return null;
    }

    @Override
    public Object doWhenOccurException(ExportTaskDTO dto, Throwable e) {

        //更新hpfm_export_task表
        dto.setState(AsyncTaskState.DONE);
        dto.setErrorInfo(e.getMessage());
        dto.setEndDateTime(new Date());
        exportTaskService.update(dto);

        return null;
    }
}
