package org.hzero.export;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.core.async.AsyncTemplate;
import org.hzero.core.export.ExportAsyncTemplate;
import org.hzero.core.export.ExportTaskDTO;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.FileType;
import org.hzero.export.download.FileDownloader;
import org.hzero.export.download.HttpFileDownloader;
import org.hzero.export.exporter.CsvExporter;
import org.hzero.export.exporter.ExcelExporter;
import org.hzero.export.exporter.IFileExporter;
import org.hzero.export.util.ResponseWriter;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportParam;
import org.hzero.export.vo.ExportProperty;

/**
 * excel export data helper.
 *
 * @author bojiangzhou 2018/07/25
 */
public class ExportDataHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataHelper.class);

    private static final String EXPORT_PARAM_MUST_NOT_BE_NULL = "ExportParam must not be null.";

    private final ExportColumnHelper exportColumnHelper;
    private ThreadPoolTaskExecutor executorService;
    private ExportFutureManager futureManager;
    @Autowired(required = false)
    private ExportAsyncTemplate exportAsyncTemplate;
    @Autowired
    private ExportProperties properties;

    @Value("${spring.application.name:}")
    private String serviceName;
    @Value("${management.server.port:8081}")
    private String port;
    @Value("${server.hostname:}")
    private String hostname;

    public ExportDataHelper(ExportColumnHelper exportColumnHelper) {
        this.exportColumnHelper = exportColumnHelper;
    }

    public ExportDataHelper(ExportColumnHelper exportColumnHelper,
                            ThreadPoolTaskExecutor executorService,
                            ExportFutureManager futureManager) {
        this.exportColumnHelper = exportColumnHelper;
        this.executorService = executorService;
        this.futureManager = futureManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void exportFile(ProceedingJoinPoint joinPoint, ExcelExport excelExport, HttpServletResponse response, FileType fileType) {
        // 获取拦截的目标对象和方法，用于后续请求数据.
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 分页参数
        PageRequest pageRequest = null;
        // 导出参数
        ExportParam exportParam = null;
        for (Object arg : args) {
            if (arg instanceof PageRequest) {
                pageRequest = (PageRequest) arg;
            } else if (arg instanceof ExportParam) {
                exportParam = (ExportParam) arg;
            }
        }
        if (exportParam == null) {
            LOGGER.error(EXPORT_PARAM_MUST_NOT_BE_NULL);
            throw new IllegalArgumentException(EXPORT_PARAM_MUST_NOT_BE_NULL);
        }
        // 指定了业务对象，默认将编码写入隐藏sheet
        if (StringUtils.isNotBlank(exportParam.getExportTemplateCode())) {
            exportParam.setCodeRenderMode(CodeRender.SHEET);
        }

        // 获取导出定义
        ExportColumn root = exportColumnHelper.getCheckedExportColumn(excelExport, exportParam);
        // 根节点必须被选中
        Assert.isTrue(root.isChecked(), "export.column.least-one");

        Set<String> selection = exportColumnHelper.getSelection(root);
        exportParam.setSelection(selection);

        // 单sheet最大行数
        int singleMaxRow = exportParam.getSingleSheetMaxRow() == null ? fileType.getMaxRow() : exportParam.getSingleSheetMaxRow();
        if (singleMaxRow > fileType.getMaxRow()) {
            // 单sheet最大行数不能超多文件本身的限制
            singleMaxRow = fileType.getMaxRow();
        }
        // excel 最大sheet页数
        int singleMaxPage = exportParam.getSingleExcelMaxSheetNum() == null ? properties.getSingleExcelMaxSheetNum() : exportParam.getSingleExcelMaxSheetNum();
        ExportProperty exportProperty = buildExportProperty(exportParam, excelExport);
        IFileExporter fileExporter;
        if (FileType.CSV.equals(fileType)) {
            fileExporter = new CsvExporter(root, exportParam.getFillerType(), singleMaxPage, singleMaxRow, fileType, exportProperty);
        } else {
            // excel导出
            fileExporter = new ExcelExporter(root, exportParam.getFillerType(), singleMaxPage, singleMaxRow, fileType, exportProperty);
        }
        String defaultFilename = StringUtils.isBlank(excelExport.defaultFileName()) ? exportParam.getFileName() : excelExport.defaultFileName();
        FileDownloader downloader = new HttpFileDownloader(defaultFilename, response, fileExporter);

        // 如果不指定文件名，则使用导出模版主题名
        String filename = downloader.getFileName();

        // 异步时传递CustomUserDetails
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        boolean async = determineAsync(exportParam, properties);
        if (pageRequest != null) {
            doPageExport(pageRequest, root, async, filename, target, method, args, fileExporter, userDetails, response, downloader, excelExport, exportParam);
        } else {
            doNotPageExport(async, filename, target, method, args, fileExporter, userDetails, response, downloader, excelExport, exportParam);
        }
    }

    private void doPageExport(PageRequest pageRequest, ExportColumn root, boolean async, String filename, Object target,
                              Method method, Object[] args, IFileExporter fileExporter, CustomUserDetails userDetails,
                              HttpServletResponse response, FileDownloader downloader, ExcelExport excelExport, ExportParam exportParam) {
        // 导出数量限制
        Long limit = getExportLimit(excelExport, exportParam);
        // 分页查询
        pageRequest.setSize(root.getExcelSheetProperty() == null ? ExportConstants.PAGE_SIZE : root.getExcelSheetProperty().getPageSize());
        if (async) {
            asyncExportAndResponse(filename, null, target, method, args, pageRequest, fileExporter, userDetails, response, limit);
        } else {
            // 同步查询，考虑是否需要转为异步执行
            Page<?> data;
            long totalElements = 0;
            try {
                data = (Page<?>) requestData(target, method, args);
                totalElements = data.getTotalElements();
            } catch (Exception e) {
                LOGGER.error("export error!", e);
                if (e instanceof InvocationTargetException) {
                    Throwable throwable = ((InvocationTargetException) e).getTargetException();
                    fileExporter.setError(throwable.getMessage());
                } else {
                    fileExporter.setError(e.getMessage());
                }
                downloader.download();
                return;
            }
            // 校验导出数据量
            checkDataCount(limit, totalElements);
            // 如果配置了异步阈值，当超过异步阈值时，自动转为异步导出
            Integer asyncThreshold = properties.getAsyncThreshold();
            if (excelExport.asyncThreshold() > 0) {
                // 注解指定异步阈值优先级高于配置文件
                asyncThreshold = excelExport.asyncThreshold();
            }
            if (asyncThreshold != null && totalElements > asyncThreshold) {
                asyncExportAndResponse(filename, data, target, method, args, pageRequest, fileExporter, userDetails, response, limit);
            } else {
                try {
                    // 填充第一页数据
                    fillData(data, fileExporter, pageRequest);
                    while (pageRequest.getPage() < data.getTotalPages() - 1) {
                        pageRequest.setPage(pageRequest.getPage() + 1);
                        data = (Page<?>) requestData(target, method, args);
                        // 循环填充数据
                        fillData(data, fileExporter, pageRequest);
                    }
                } catch (Exception e) {
                    fileExporter.setError(e.getMessage());
                }
                downloader.download();
            }
        }
    }

    private void doNotPageExport(boolean async, String filename, Object target, Method method, Object[] args,
                                 IFileExporter fileExporter, CustomUserDetails userDetails, HttpServletResponse response,
                                 FileDownloader downloader, ExcelExport excelExport, ExportParam exportParam) {
        // 导出数量限制
        Long limit = getExportLimit(excelExport, exportParam);
        // 不分页
        if (async) {
            asyncExportAndResponse(filename, null, target, method, args, null, fileExporter, userDetails, response, limit);
        } else {
            // 同步查询，考虑是否需要转为异步执行
            List<?> data;
            try {
                data = requestData(target, method, args);
            } catch (Exception e) {
                LOGGER.error("export error!", e);
                if (e instanceof InvocationTargetException) {
                    Throwable throwable = ((InvocationTargetException) e).getTargetException();
                    fileExporter.setError(throwable.getMessage());
                } else {
                    fileExporter.setError(e.getMessage());
                }
                downloader.download();
                return;
            }
            // 校验导出数据量
            checkDataCount(limit, (long) data.size());
            // 如果配置了异步阈值，当超过异步阈值时，同步转异步
            Integer asyncThreshold = properties.getAsyncThreshold();
            if (excelExport.asyncThreshold() > 0) {
                // 注解指定异步阈值优先级高于配置文件
                asyncThreshold = excelExport.asyncThreshold();
            }
            if (asyncThreshold != null && data.size() > asyncThreshold) {
                asyncExportAndResponse(filename, data, target, method, args, null, fileExporter, userDetails, response, limit);
            } else {
                try {
                    fillData(data, fileExporter, null);
                } catch (Exception e) {
                    fileExporter.setError(e.getMessage());
                }
                downloader.download();
            }
        }
    }

    private Long getExportLimit(ExcelExport excelExport, ExportParam exportParam) {
        // 请求指定的限制数量优先级高于注解
        Long limit = exportParam.getMaxDataCount();
        if (limit == null) {
            limit = excelExport.maxDataCount();
        }
        return limit;
    }

    /**
     * 校验导出的最大数据量
     */
    private void checkDataCount(Long limit, Long count) {
        // 数据总量超过了最大限制
        if (limit > 0 && count > limit) {
            throw new CommonException("export.error.too-many-data");
        }
    }

    /**
     * 判断是否异步执行
     */
    private boolean determineAsync(ExportParam exportParam, ExportProperties properties) {
        if (ExportProperties.ASYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())) {
            return true;
        } else if (ExportProperties.SYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())) {
            return false;
        } else {
            return exportParam.isAsync();
        }
    }

    private void asyncExportAndResponse(String filename,
                                        List<?> data,
                                        Object target,
                                        Method method,
                                        Object[] args,
                                        PageRequest pageRequest,
                                        IFileExporter fileExporter,
                                        CustomUserDetails userDetails,
                                        HttpServletResponse response,
                                        Long limit) {
        // 构建导出任务对象
        ExportTaskDTO dto = constructExportTaskDto(filename, getLocalhost(), serviceName);
        String uuid = asyncExecute(new ExportTask(data, target, method, args, pageRequest, fileExporter,
                exportAsyncTemplate, dto, limit, userDetails, filename));
        ResponseWriter.writeAsyncRequestSuccess(response, uuid);
    }

    private String getLocalhost() {
        if ("".equals(hostname)) {
            String host;
            try {
                host = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new CommonException("Export task execute failed", e);
            }
            return host + ":" + port;
        }
        return hostname + ":" + port;
    }

    private ExportTaskDTO constructExportTaskDto(String taskName, String hostName, String serviceName) {
        ExportTaskDTO dto = new ExportTaskDTO();
        dto.setTaskName(taskName);
        dto.setHostName(hostName);
        dto.setServiceName(serviceName);
        dto.setTenantId(0L);
        dto.setUserId(0L);
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails != null) {
            dto.setTenantId(Optional.ofNullable(userDetails.getTenantId()).orElse(0L));
            dto.setUserId(Optional.ofNullable(userDetails.getUserId()).orElse(0L));
        }
        return dto;
    }

    private String asyncExecute(ExportTask exportTask) {
        if (exportAsyncTemplate == null) {
            throw new IllegalStateException("请确保已引入组件 [hzero-boot-platform / hzero-boot-file].");
        }
        // 生成异步导出记录
        UUID uuid = UUID.randomUUID();
        exportTask.getDto().setTaskCode(uuid.toString());
        // 执行异步导出，并在内存记录
        Future<?> future = executorService.submit(exportTask);
        futureManager.put(uuid, future);
        // 开始执行异步导出钩子，默认在boot-platform实现了
        exportAsyncTemplate.afterSubmit(exportTask.getDto());
        return uuid.toString();
    }

    public class ExportTask implements Runnable {

        private List<?> prefetchData;
        private final Object target;
        private final Method method;
        private final Object[] args;
        private final PageRequest pageRequest;
        private final IFileExporter fileExporter;
        private final ExportAsyncTemplate exportAsyncTemplate;
        private final ExportTaskDTO dto;
        private final Long limit;
        private final CustomUserDetails userDetails;
        private final String filename;

        ExportTask(List<?> prefetchData,
                   Object target,
                   Method method,
                   Object[] args,
                   PageRequest pageRequest,
                   IFileExporter fileExporter,
                   ExportAsyncTemplate exportAsyncTemplate,
                   ExportTaskDTO dto,
                   Long limit,
                   CustomUserDetails userDetails,
                   String filename) {
            this.prefetchData = prefetchData;
            this.target = target;
            this.method = method;
            this.args = args;
            this.pageRequest = pageRequest;
            this.fileExporter = fileExporter;
            this.exportAsyncTemplate = exportAsyncTemplate;
            this.dto = dto;
            this.limit = limit;
            this.userDetails = userDetails;
            this.filename = filename;
        }

        @Override
        public void run() {
            if (userDetails != null) {
                DetailsHelper.setCustomUserDetails(userDetails);
            }
            try {
                // 数据总数
                int count = 0;
                if (CollectionUtils.isEmpty(prefetchData)) {
                    // 之前没有查询过数据，执行一次查询
                    prefetchData = requestData(target, method, args, dto);
                    // 校验导出数据量
                    if (pageRequest != null) {
                        checkDataCount(limit, ((Page<?>) prefetchData).getTotalElements());
                    } else {
                        checkDataCount(limit, (long) prefetchData.size());
                    }
                }
                count += prefetchData.size();
                fillData(prefetchData, fileExporter, pageRequest);
                if (pageRequest != null) {
                    Page<?> data = (Page<?>) prefetchData;
                    while (pageRequest.getPage() < data.getTotalPages() - 1) {
                        pageRequest.setPage(pageRequest.getPage() + 1);
                        data = (Page<?>) requestData(target, method, args, dto);
                        count += data.getContent().size();
                        fillData(data, fileExporter, pageRequest);
                    }
                }
                String suffix = fileExporter.getOutputFileSuffix();
                String fileType;
                if (".xlsx".equals(suffix)) {
                    fileType = ExportAsyncTemplate.FILE_TYPE_EXCEL;
                } else if (".zip".equals(suffix)) {
                    fileType = ExportAsyncTemplate.FILE_TYPE_ZIP;
                } else {
                    throw new CommonException("Not support file type!");
                }
                // 异步导出执行完成钩子，默认在boot-platform实现了文件上传
                Map<String, Object> additionInfo = new HashMap<>(4);
                additionInfo.put("fileType", fileType);
                additionInfo.put("file", fileExporter.exportBytes());
                exportAsyncTemplate.doWhenFinish(dto, additionInfo);
            } catch (Exception e) {
                LOGGER.error("export task execute error", e);
                // 异步导出执行异常钩子，默认在boot-platform实现
                exportAsyncTemplate.doWhenOccurException(dto, e);
            } finally {
                try {
                    fileExporter.close();
                } catch (Exception e) {
                    LOGGER.error("The temp file clean failed.", e);
                } finally {
                    futureManager.remove(dto.getTaskCode());
                }
            }
        }

        public ExportTaskDTO getDto() {
            return dto;
        }

    }


    /**
     * 导出模板
     */
    public void exportTemplate(ProceedingJoinPoint joinPoint, ExcelExport excelExport, HttpServletResponse response, FileType fileType) {
        Object[] args = joinPoint.getArgs();

        ExportParam exportParam = null;
        for (Object arg : args) {
            if (arg instanceof ExportParam) {
                exportParam = (ExportParam) arg;
            }
        }
        if (exportParam == null) {
            LOGGER.warn(EXPORT_PARAM_MUST_NOT_BE_NULL);
            throw new IllegalArgumentException(EXPORT_PARAM_MUST_NOT_BE_NULL);
        }

        ExportColumn root = exportColumnHelper.getCheckedExportColumn(excelExport, exportParam);
        if (!root.isChecked()) {
            throw new CommonException("export.column.least-one");
        }

        // 单sheet最大行数
        int singleMaxRow = exportParam.getSingleSheetMaxRow() == null ? fileType.getMaxRow() : exportParam.getSingleSheetMaxRow();
        if (singleMaxRow > fileType.getMaxRow()) {
            // 单sheet最大行数不能超多文件本身的限制
            singleMaxRow = fileType.getMaxRow();
        }
        // excel 最大sheet页数
        int singleMaxPage = exportParam.getSingleExcelMaxSheetNum() == null ? properties.getSingleExcelMaxSheetNum() : exportParam.getSingleExcelMaxSheetNum();
        ExportProperty exportProperty = buildExportProperty(exportParam, excelExport);
        IFileExporter fileExporter;
        if (FileType.CSV.equals(fileType)) {
            fileExporter = new CsvExporter(root, exportParam.getFillerType(), singleMaxPage, singleMaxRow, fileType, exportProperty);
        } else {
            // excel导出
            fileExporter = new ExcelExporter(root, exportParam.getFillerType(), singleMaxPage, singleMaxRow, fileType, exportProperty);
        }
        String filename = StringUtils.isBlank(excelExport.defaultFileName()) ? exportParam.getFileName() : excelExport.defaultFileName();
        FileDownloader downloader = new HttpFileDownloader(filename, response, fileExporter);
        // 填充标题
        fileExporter.fillTitle();
        // 下载文件
        downloader.download();
    }

    private List<?> requestData(Object target, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return requestData(target, method, args, null);
    }

    private List<?> requestData(Object target, Method method, Object[] args, ExportTaskDTO exportTaskDTO) throws InvocationTargetException, IllegalAccessException {
        // 请求数据
        Object result = method.invoke(target, args);
        List<?> data = null;
        if (result instanceof List) {
            data = (List<?>) result;
        } else if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            if (responseEntity.getBody() instanceof List) {
                data = (List<?>) responseEntity.getBody();
            }
        }
        if (data == null) {
            String message = "Response data type must be [? extends java.util.List] or ResponseEntity<? extends java.util.List>";
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        return data;
    }

    private void fillData(List<?> data, IFileExporter excelExporter, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(data)) {
            // 没有数据则默认导出模板
            excelExporter.fillTitle();
            return;
        }
        if (pageRequest == null) {
            // 填充数据
            excelExporter.fillData(data);
            data.clear();
            return;
        } else if (data instanceof Page) {
            Page<?> pageData = (Page<?>) data;
            excelExporter.fillData(pageData.getContent());
            if (pageData.getContent() != null) {
                pageData.getContent().clear();
            }
            return;
        }
        String message = "Response data type must be [io.choerodon.core.domain.Page] or ResponseEntity<Page>";
        LOGGER.warn(message);
        throw new IllegalArgumentException(message);
    }

    /**
     * 构建导出过程参数
     */
    private ExportProperty buildExportProperty(ExportParam exportParam, ExcelExport excelExport) {
        // 接口指定的渲染方式优先级高于注解
        CodeRender codeRender = exportParam.getCodeRenderMode();
        if (codeRender == null) {
            codeRender = excelExport.codeRenderMode();
        }
        return new ExportProperty().setCodeRenderMode(codeRender);
    }
}

