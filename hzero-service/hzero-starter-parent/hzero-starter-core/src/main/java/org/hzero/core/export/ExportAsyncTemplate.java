package org.hzero.core.export;


import org.hzero.core.async.AsyncTemplate;

/**
 * 异步导出模版
 * @author XCXCXCXCX
 * @date 2019/8/5
 */
public interface ExportAsyncTemplate extends AsyncTemplate<ExportTaskDTO> {

    String FILE_TYPE_ZIP = "zip";
    String FILE_TYPE_EXCEL = "excel";

    String FILE_TYPE_KEY = "fileType";
    String FILE_KEY = "file";

}


