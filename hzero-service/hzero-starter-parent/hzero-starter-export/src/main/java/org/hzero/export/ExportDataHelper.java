package org.hzero.export;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hzero.core.export.ExportAsyncTemplate;
import org.hzero.core.export.ExportTaskDTO;
import org.hzero.core.variable.RequestVariableHolder;
import org.hzero.export.annotation.ExcelExport;
import org.hzero.export.download.HttpExcelDownloader;
import org.hzero.export.exporter.ExcelExporter;
import org.hzero.export.util.ResponseWriter;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * excel export data helper.
 *
 * @author bojiangzhou 2018/07/25
 */
public class ExportDataHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDataHelper.class);

    private static final String EXPORT_PARAM_MUST_NOT_BE_NULL = "ExportParam must not be null.";

    private ExportColumnHelper exportColumnHelper;
    private ExecutorService executorService;
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

    public ExportDataHelper() {
    }

    public ExportDataHelper(ExportColumnHelper exportColumnHelper) {
        this.exportColumnHelper = exportColumnHelper;
    }

    public ExportDataHelper(ExportColumnHelper exportColumnHelper,
                            ExecutorService executorService,
                            ExportFutureManager futureManager) {
        this.exportColumnHelper = exportColumnHelper;
        this.executorService = executorService;
        this.futureManager = futureManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    public void exportExcel(ProceedingJoinPoint joinPoint, ExcelExport excelExport, HttpServletResponse response) throws Exception {
        // 获取拦截的目标对象和方法，用于后续请求数据.
        Object target = joinPoint.getTarget();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();

        PageRequest pageRequest = null;
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

        ExportColumn root = exportColumnHelper.getCheckedExportColumn(excelExport, Optional.ofNullable(exportParam.getIds()).orElse(Collections.emptySet()));
        if (!root.isChecked()) {
            throw new CommonException("export.column.least-one");
        }

        Set<String> selection = exportColumnHelper.getSelection(excelExport, exportParam.getIds());
        exportParam.setSelection(selection);

        boolean paging = pageRequest != null;

        int singleSheetMaxRow = exportParam.getSingleSheetMaxRow() == null ? properties.getSingleSheetMaxRow() : exportParam.getSingleSheetMaxRow();
        int singleExcelMaxSheetNum = exportParam.getSingleExcelMaxSheetNum() == null ? properties.getSingleExcelMaxSheetNum() : exportParam.getSingleExcelMaxSheetNum();
        IExcelExporter excelExporter = new ExcelExporter(root, exportParam.getFillerType(), singleExcelMaxSheetNum, singleSheetMaxRow);
        ExcelDownloader downloader = new HttpExcelDownloader(exportParam.getFileName(),
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest(), response, excelExporter);

        // 如果不指定文件名，则使用导出模版主题名
        String fileName = downloader.getFileName();

        //异步时传递CustomUserDetails
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        boolean async = determineAsync(exportParam, properties);
        if (paging) {
            pageRequest.setSize(root.getExcelSheet().pageSize());
            if (async) {
                asyncExportAndResponse(fileName, null, target, method, args, pageRequest, excelExporter, userDetails, response);
            } else {
                // 同步查询，考虑是否需要转为异步执行
                Page<?> data;
                try {
                    data = (Page<?>) requestData(target, method, args);
                } catch (Throwable e) {
                    excelExporter.setError(e.getMessage());
                    downloader.download();
                    return;
                }
                // 如果配置了异步阈值，当超过异步阈值时，同步转异步
                if (properties.getAsyncThreshold() != null && data.getTotalElements() > properties.getAsyncThreshold()) {
                    asyncExportAndResponse(fileName, data, target, method, args, pageRequest, excelExporter, userDetails, response);
                } else {
                    try {
                        fillSheet(data, excelExporter, pageRequest);
                        while (pageRequest.getPage() < data.getTotalPages() - 1) {
                            pageRequest.setPage(pageRequest.getPage() + 1);
                            data = (Page<?>) requestData(target, method, args);
                            fillSheet(data, excelExporter, pageRequest);
                        }
                    } catch (Throwable e) {
                        excelExporter.setError(e.getMessage());
                    }
                    downloader.download();
                    return;
                }
            }
        } else {
            if (async) {
                asyncExportAndResponse(fileName, null, target, method, args, null, excelExporter, userDetails, response);
            } else {
                // 同步查询，考虑是否需要转为异步执行
                List<?> data;
                try {
                    data = requestData(target, method, args);
                } catch (Throwable e) {
                    excelExporter.setError(e.getMessage());
                    downloader.download();
                    return;
                }
                // 如果配置了异步阈值，当超过异步阈值时，同步转异步
                if (properties.getAsyncThreshold() != null && data.size() > properties.getAsyncThreshold()) {
                    asyncExportAndResponse(fileName, data, target, method, args, null, excelExporter, userDetails, response);
                } else {
                    try {
                        fillSheet(data, excelExporter, null);
                    } catch (Throwable e) {
                        excelExporter.setError(e.getMessage());
                    }
                    downloader.download();
                    return;
                }
            }
        }

    }

    private boolean determineAsync(ExportParam exportParam, ExportProperties properties) {
        if (ExportProperties.ASYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())) {
            return true;
        } else if (ExportProperties.SYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())) {
            return false;
        } else {
            return exportParam.isAsync();
        }
    }

    private void asyncExportAndResponse(String fileName,
                                        List<?> data,
                                        Object target,
                                        Method method,
                                        Object[] args,
                                        PageRequest pageRequest,
                                        IExcelExporter excelExporter,
                                        CustomUserDetails userDetails,
                                        HttpServletResponse response) {
        ExportTaskDTO dto = constructExportTaskDto(fileName, getLocalhost(), serviceName);
        String uuid = asyncExecute(new ExportTask(data, target, method, args,
                pageRequest, excelExporter, exportAsyncTemplate, dto,
                () -> {
                    DetailsHelper.setCustomUserDetails(userDetails);
                    if (userDetails != null) {
                        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
                            HystrixRequestContext.initializeContext();
                        }
                        RequestVariableHolder.TENANT_ID.set(userDetails.getOrganizationId());
                        RequestVariableHolder.USER_ID.set(userDetails.getUserId());
                    }
                },
                () -> {
                    RequestVariableHolder.TENANT_ID.remove();
                    RequestVariableHolder.USER_ID.remove();
                }));
        ResponseWriter.writeAsyncRequestSuccess(response, uuid);
    }

    private String getLocalhost() {
        if ("".equals(hostname)) {
            String hostname;
            try {
                hostname = Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException("Export task execute failed", e);
            }
            return hostname + ":" + port;
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
            throw new IllegalStateException("请确保已引入异步支持组件 [org.hzero.core.export.ExportAsyncTemplate].");
        }
        UUID uuid = UUID.randomUUID();
        exportTask.getDto().setTaskCode(uuid.toString());
        return exportAsyncTemplate.submit(exportTask.getDto(), () -> {
            Future future = executorService.submit(exportTask);
            futureManager.put(uuid, future);
            return uuid.toString();
        });
    }

    public class ExportTask implements Runnable {

        private List<?> prefetchData;
        private Object target;
        private Method method;
        private Object[] args;
        private PageRequest pageRequest;
        private IExcelExporter excelExporter;
        private ExportAsyncTemplate exportAsyncTemplate;

        private ExportTaskDTO dto;

        private Initialization initialization;

        private Finalization finalization;

        ExportTask(List<?> prefetchData,
                   Object target,
                   Method method,
                   Object[] args,
                   PageRequest pageRequest,
                   IExcelExporter excelExporter,
                   ExportAsyncTemplate exportAsyncTemplate,
                   ExportTaskDTO dto,
                   Initialization initialization,
                   Finalization finalization) {
            this.prefetchData = prefetchData;
            this.target = target;
            this.method = method;
            this.args = args;
            this.pageRequest = pageRequest;
            this.excelExporter = excelExporter;
            this.exportAsyncTemplate = exportAsyncTemplate;
            this.dto = dto;
            this.initialization = initialization;
            this.finalization = finalization;
        }

        @Override
        public void run() {
            try {
                initialization.init();
                if (CollectionUtils.isEmpty(prefetchData)) {
                    prefetchData = requestData(target, method, args);
                }
                fillSheet(prefetchData, excelExporter, pageRequest);
                if (pageRequest != null) {
                    Page<?> data = (Page<?>) prefetchData;
                    while (pageRequest.getPage() < data.getTotalPages() - 1) {
                        pageRequest.setPage(pageRequest.getPage() + 1);
                        data = (Page<?>) requestData(target, method, args);
                        fillSheet(data, excelExporter, pageRequest);
                    }
                }
                byte[] file = excelExporter.exportBytes();
                String fileType = excelExporter.getOutputFileSuffix().equals(IExcelExporter.ZIP_SUFFIX) ? ExportAsyncTemplate.FILE_TYPE_ZIP : ExportAsyncTemplate.FILE_TYPE_EXCEL;
                Map<String, Object> additionInfo = new HashMap<>(2);
                additionInfo.put("fileType", fileType);
                additionInfo.put("file", file);
                //文件上传优化方案
                //InputStream fileInputStream = excelExporter.readInputStream();
                //additionInfo.put("fileInputStream", fileInputStream);
                exportAsyncTemplate.doWhenFinish(dto, additionInfo);
            } catch (Throwable e) {
                LOGGER.error("export task execute error", e);
                exportAsyncTemplate.doWhenOccurException(dto, e);
            } finally {
                try {
                    excelExporter.close();
                } catch (Exception e) {
                    LOGGER.error("The temp file clean failed.", e);
                } finally {
                    finalization.finish();
                    futureManager.remove(dto.getTaskCode());
                }
            }
        }

        public ExportTaskDTO getDto() {
            return dto;
        }

    }

    @FunctionalInterface
    interface Initialization {
        /**
         * 任务执行前的初始化操作
         */
        void init();
    }

    @FunctionalInterface
    interface Finalization {
        /**
         * 任务执行后的兜底操作
         */
        void finish();
    }


    /**
     * 导出模板
     */
    public void exportTemplate(ProceedingJoinPoint joinPoint, ExcelExport excelExport, HttpServletResponse response) throws Exception {
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

        ExportColumn root = exportColumnHelper.getCheckedExportColumn(excelExport,
                Optional.ofNullable(exportParam.getIds()).orElse(Collections.emptySet()));
        if (!root.isChecked()) {
            throw new CommonException("export.column.least-one");
        }

        int singleSheetMaxRow = exportParam.getSingleSheetMaxRow() == null ? properties.getSingleSheetMaxRow() : exportParam.getSingleSheetMaxRow();
        int singleExcelMaxSheetNum = exportParam.getSingleExcelMaxSheetNum() == null ? properties.getSingleExcelMaxSheetNum() : exportParam.getSingleExcelMaxSheetNum();
        IExcelExporter excelExporter = new ExcelExporter(root, exportParam.getFillerType(), singleExcelMaxSheetNum, singleSheetMaxRow);
        ExcelDownloader downloader = new HttpExcelDownloader(null,
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest(), response, excelExporter);
        downloader.download();
    }

    private List<?> requestData(Object target, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        // 请求数据
        Object result = method.invoke(target, args);
        List<?> data = null;
        if (result instanceof List) {
            data = (List<?>) result;
        } else if (result instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) result;
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

    private void fillSheet(List<?> data, IExcelExporter excelExporter, PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(data)) {
            // 没有数据则默认导出模板
            excelExporter.fillTitle();
            return;
        }
        if (pageRequest == null) {
            // 填充数据
            excelExporter.fillSheet(data);
            return;
        } else if (data instanceof Page) {
            Page<?> pageData = (Page<?>) data;
            excelExporter.fillSheet(pageData.getContent());
            if (pageData.getContent() != null) {
                pageData.getContent().clear();
            }
            return;
        }
        String message = "Response data type must be [io.choerodon.core.domain.Page] or ResponseEntity<Page>";
        LOGGER.warn(message);
        throw new IllegalArgumentException(message);
    }

}

