package org.hzero.report.domain.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.file.constant.FileType;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.core.util.TokenUtils;
import org.hzero.report.app.service.ReportRequestService;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.service.*;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.Query;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.hzero.report.infra.enums.TemplateTypeEnum;
import org.hzero.report.infra.util.CustomTokenUtils;
import org.hzero.report.infra.util.DownLoadUtils;
import org.hzero.report.infra.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 报表处理领域服务实现类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月23日下午8:03:01
 */
@Service
public class ReportGenerateServiceImpl implements IReportGenerateService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileClient fileClient;
    private final ReportConfig reportConfig;
    private final RedisQueueHelper redisQueueHelper;
    private final IReportMetaService reportMetaService;
    private final ITableReportService tableReportService;
    private final IChartReportService chartReportService;
    private final ReportRequestService reportRequestService;
    private final IDocumentReportService documentReportService;
    private final IUreportService ureportService;

    @Autowired
    public ReportGenerateServiceImpl(FileClient fileClient,
                                     ReportConfig reportConfig,
                                     RedisQueueHelper redisQueueHelper,
                                     IReportMetaService reportMetaService,
                                     ITableReportService tableReportService,
                                     IChartReportService chartReportService,
                                     ReportRequestService reportRequestService,
                                     IDocumentReportService documentReportService,
                                     IUreportService ureportService) {
        this.fileClient = fileClient;
        this.reportConfig = reportConfig;
        this.redisQueueHelper = redisQueueHelper;
        this.reportMetaService = reportMetaService;
        this.tableReportService = tableReportService;
        this.chartReportService = chartReportService;
        this.reportRequestService = reportRequestService;
        this.documentReportService = documentReportService;
        this.ureportService = ureportService;
    }

    @Override
    public ReportDataSet getReportDataSet(Report report, Map<String, Object> formParameters) {
        return tableReportService.getReportDataSet(report, formParameters);
    }

    @Override
    public void generate(String reportUuid, JSONObject data, HttpServletRequest request) {
        Report report = reportMetaService.getReportByKey(reportUuid);
        Assert.notNull(report, HrptMessageConstants.ERROR_REPORT_NOT_EXIST);
        generate(report, data, request);
    }

    @Override
    public void generate(Report report, JSONObject data, HttpServletRequest request) {
        ReportTypeEnum reportTypeEnum = ReportTypeEnum.valueOf2(report.getReportTypeCode());
        switch (reportTypeEnum) {
            case SIMPLE_TABLE:
                generateSimpleTable(report, data, request.getParameterMap());
                break;
            case TABLE:
                generateTable(report, data, request.getParameterMap());
                break;
            case CHART:
                generateChart(report, data, request.getParameterMap());
                break;
            case DOCUMENT:
                generateDocument(report, data, request.getParameterMap());
                break;
            case UREPORT:
                ureportService.previewReport(report, data, request);
                break;
            default:
                break;
        }
    }

    @Override
    public void generateSimpleTable(Report report, JSONObject data, Map<?, ?> parameters) {
        Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), parameters);
        boolean b = reportMetaService.isAsyncExecute(report.getTenantId(), report.getDatasourceCode(), report.getSqlText(), formParams, report.getLimitRows(), report.getAsyncFlag());
        if (b) {
            this.asyncExportReportFile(report, formParams);
            data.put(HrptConstants.HTML_TABLE, "<p><h1>" + MessageAccessor.getMessage(HrptMessageConstants.INFO_ASYNC_REPORT_REQUEST).desc() + "</h1></p>");
        } else {
            ReportTable reportTable = tableReportService.getSimpleReportTable(report, formParams);
            data.put(HrptConstants.HTML_TABLE, reportTable.getHtmlText());
            data.put(HrptConstants.TableData.META_DATA_PAGE_SIZE, reportTable.getMetaDataPageSize());
            data.put(HrptConstants.TableData.META_DATA_ROW_TOTAL, reportTable.getMetaDataRowTotal());
        }
    }

    @Override
    public void generateTable(Report report, JSONObject data, Map<?, ?> parameters) {
        Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), parameters);
        boolean b = reportMetaService.isAsyncExecute(report.getTenantId(), report.getDatasourceCode(), report.getSqlText(), formParams,
                report.getLimitRows(), report.getAsyncFlag());
        if (b) {
            this.asyncExportReportFile(report, formParams);
            data.put(HrptConstants.HTML_TABLE, "<p><h1>" + MessageAccessor.getMessage(HrptMessageConstants.INFO_ASYNC_REPORT_REQUEST).desc() + "</h1></p>");
        } else {
            ReportTable reportTable = generateTable(report, formParams);
            data.put(HrptConstants.HTML_TABLE, reportTable.getHtmlText());
            data.put(HrptConstants.TableData.META_DATA_PAGE_SIZE, reportTable.getMetaDataPageSize());
            data.put(HrptConstants.TableData.META_DATA_ROW_TOTAL, reportTable.getMetaDataRowTotal());
        }
    }

    @Override
    public void generateTable(Query queryer, ReportParameter reportParameter, JSONObject data) {
        ReportTable reportTable = tableReportService.getReportTable(queryer, reportParameter);
        data.put(HrptConstants.HTML_TABLE, reportTable.getHtmlText());
        data.put(HrptConstants.TableData.META_DATA_ROW_COUNT, reportTable.getMetaDataRowCount());
    }

    @Override
    public void generateTable(MetaDataSet metaDataSet, ReportParameter reportParameter, JSONObject data) {
        ReportTable reportTable = tableReportService.getReportTable(metaDataSet, reportParameter);
        data.put(HrptConstants.HTML_TABLE, reportTable.getHtmlText());
        data.put(HrptConstants.TableData.META_DATA_ROW_COUNT, reportTable.getMetaDataRowCount());
    }

    @Override
    public ReportTable generateTable(Report report, Map<String, Object> formParams) {
        return tableReportService.getReportTable(report, formParams);
    }

    @Override
    public List<MetaDataRow> generateTableRows(Report report, Map<String, Object> formParams) {
        return tableReportService.getReportTableRows(report, formParams);
    }

    @Override
    public void generateChart(Report report, JSONObject data, Map<?, ?> parameters) {
        // 初始化图表数据
        this.getDefaultChartData(data);
        // 组装图形报表数据
        Map<String, Object> formParameters = reportMetaService.getFormParameters(report.getDatasetId(), parameters);
        final ReportDataSet reportDataSet = tableReportService.getReportDataSet(report, formParameters);
        data.put(HrptConstants.ChartData.DIM_COLUMN_MAP, chartReportService.getDimColumnMap(reportDataSet));
        data.put(HrptConstants.ChartData.DIM_COLUMNS, chartReportService.getDimColumns(reportDataSet));
        data.put(HrptConstants.ChartData.STAT_COLUMNS, chartReportService.getStatColumns(reportDataSet));
        data.put(HrptConstants.ChartData.DATA_ROWS, chartReportService.getDataRows(reportDataSet));
    }

    @Override
    public void getDefaultChartData(JSONObject data) {
        data.put(HrptConstants.ChartData.DIM_COLUMN_MAP, null);
        data.put(HrptConstants.ChartData.DIM_COLUMNS, null);
        data.put(HrptConstants.ChartData.STAT_COLUMNS, null);
        data.put(HrptConstants.ChartData.DATA_ROWS, null);
        data.put(HrptConstants.REPORT_MSG, "");
    }

    @Override
    public void generateDocument(Report report, JSONObject data, Map<?, ?> parameters) {
        TemplateDtl templateDtl = getTemplateByParams(report, parameters);
        Assert.notNull(templateDtl, HrptMessageConstants.ERROR_TEMPLATE_NOT_EXIST);
        Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), parameters);
        documentReportService.generateDocument(report, templateDtl, data, formParams);
    }

    @Override
    public void exportReportFile(String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report report = reportMetaService.getReportByKey(reportUuid);
        exportReportFile(report, outputType, request, response);
    }

    private void exportReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Assert.notNull(report, HrptMessageConstants.ERROR_REPORT_NOT_EXIST);
        ReportTypeEnum reportTypeEnum = ReportTypeEnum.valueOf2(report.getReportTypeCode());
        switch (reportTypeEnum) {
            case SIMPLE_TABLE:
                exportSimpleTableReportFile(report, outputType, request, response);
                break;
            case TABLE:
                exportTableReportFile(report, outputType, request, response);
                break;
            case CHART:
                exportChartReportFile(report, outputType, request, response);
                break;
            case DOCUMENT:
                exportDocumentReportFile(report, outputType, request, response);
                break;
            case UREPORT:
                ureportService.exportReport(report, outputType, request, response);
                break;
            default:
                break;
        }
    }

    @Override
    public void exportReportFileInside(Long tenantId, String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Report report = reportMetaService.getReportIgnorePermission(tenantId, reportUuid);
        exportReportFile(report, outputType, request, response);
    }

    @Override
    public byte[] getReportFile(String reportUuid, String outputType, HttpServletRequest request, HttpServletResponse response) {
        Report report = reportMetaService.getReportByKey(reportUuid);
        Assert.notNull(report, HrptMessageConstants.ERROR_REPORT_NOT_EXIST);
        ReportTypeEnum reportTypeEnum = ReportTypeEnum.valueOf2(report.getReportTypeCode());
        switch (reportTypeEnum) {
            case SIMPLE_TABLE:
            case TABLE:
                return getTableReportFile(report, outputType, request, response);
            case CHART:
                return getChartReportFile(report, outputType, request, response);
            case DOCUMENT:
                return getDocumentReportFile(report, outputType, request, response);
            default:
                return new byte[0];
        }
    }

    @Override
    public void exportSimpleTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 简单表格的CSV单独处理
        if (HrptConstants.DocumentData.OUTPUT_FORMAT_CSV.equals(outputType)) {
            // 导出文件强制不做分页处理
            report.setPageFlag(BaseConstants.Flag.NO);
            // 报表列名称
            List<MetaDataColumn> metaColumns = JSON.parseArray(report.getMetaColumns(), MetaDataColumn.class);
            metaColumns = metaColumns.stream().sorted(Comparator.comparing(MetaDataColumn::getOrdinal))
                    .filter(item -> item.getHidden() == BaseConstants.Flag.NO).collect(Collectors.toList());
            String[] head = new String[metaColumns.size()];
            metaColumns.stream().map(MetaDataColumn::getText).collect(Collectors.toList()).toArray(head);
            // 获取表单参数
            Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), request.getParameterMap());
            // 报表行数据
            List<MetaDataRow> list = generateTableRows(report, formParams);
            // 生成csv
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            CsvWriter csvWriter = new CsvWriter(outputStream, ',', StandardCharsets.UTF_8);
            try {
                // 写入头
                csvWriter.writeRecord(head);
                // 写入数据
                for (MetaDataRow metaDataRow : list) {
                    String[] line = StringUtils.splitPreserveAllTokens(metaDataRow.getRowKey(), HrptConstants.SEPARATOR);
                    csvWriter.writeRecord(line);
                }
            } catch (Exception e) {
                throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
            } finally {
                csvWriter.close();
            }
            DownLoadUtils.downloadFile(outputStream.toByteArray(), outputType, report.getReportName(), request, response);
        } else {
            exportTableReportFile(report, outputType, request, response);
        }
    }

    @Override
    public void exportTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) {
        final JSONObject data = new JSONObject();
        // 导出文件强制不做分页处理
        report.setPageFlag(BaseConstants.Flag.NO);
        generate(report, data, request);
        // 根据下载类型处理HTML文件的样式
        String htmlText = this.buildHtmlStyle(data.getString(HrptConstants.HTML_TABLE), outputType);
        byte[] htmlData;
        if (HrptConstants.DocumentData.OUTPUT_FORMAT_XLS.equals(outputType) || HrptConstants.DocumentData.OUTPUT_FORMAT_XLSX.equals(outputType)) {
            htmlData = ExcelUtils.htmlToExcel(htmlText, report.getReportName());
        } else {
            htmlData = htmlText.getBytes(StandardCharsets.UTF_8);
        }
        DownLoadUtils.downloadFile(htmlData, outputType, report.getReportName(), request, response);
    }

    @Override
    public byte[] getTableReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) {
        final JSONObject data = new JSONObject();
        // 导出文件强制不做分页处理
        report.setPageFlag(BaseConstants.Flag.NO);
        generate(report, data, request);
        // 根据下载类型处理HTML文件的样式
        String htmlText = this.buildHtmlStyle(data.getString(HrptConstants.HTML_TABLE), outputType);
        return htmlText.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void exportChartReportFile(Report report, String outputType, HttpServletRequest request,
                                      HttpServletResponse response) {
        switch (outputType) {
            case HrptConstants.DocumentData.OUTPUT_FORMAT_XLSX:
                final JSONObject data = new JSONObject(6);
                generateChart(report, data, request.getParameterMap());
                // TODO 图表比较复杂，待后续处理
                break;
            default:
                break;
        }
    }

    @Override
    public byte[] getChartReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) {
        return new byte[0];
    }

    @Override
    public void exportDocumentReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) {
        TemplateDtl templateDtl = getTemplateByParams(report, request.getParameterMap());
        Assert.notNull(templateDtl, HrptMessageConstants.ERROR_TEMPLATE_NOT_EXIST);
        Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), request.getParameterMap());
        TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.valueOf2(report.getTemplateTypeCode());
        OutputStream os = null;
        switch (templateTypeEnum) {
            case EXCEL:
                os = documentReportService.generateDocumentByExcel(report, templateDtl, formParams);
                break;
            case HTML:
                os = documentReportService.generateDocumentByHtml(report, templateDtl, outputType, formParams);
                break;
            case RTF:
                os = documentReportService.generateDocumentByRtf(report, templateDtl, outputType, formParams);
                break;
            case DOC:
                os = documentReportService.generateDocumentByDoc(report, templateDtl, outputType, formParams);
                break;
            default:
                break;
        }
        if (os != null) {
            byte[] data = ((ByteArrayOutputStream) os).toByteArray();
            // 下载文件
            DownLoadUtils.downloadFile(data, outputType, report.getReportName(), request, response);
        }
    }

    @Override
    public byte[] getDocumentReportFile(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) {
        TemplateDtl templateDtl = getTemplateByParams(report, request.getParameterMap());
        Assert.notNull(templateDtl, HrptMessageConstants.ERROR_TEMPLATE_NOT_EXIST);
        Map<String, Object> formParams = reportMetaService.getFormParameters(report.getDatasetId(), request.getParameterMap());
        TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.valueOf2(report.getTemplateTypeCode());
        OutputStream os = null;
        switch (templateTypeEnum) {
            case EXCEL:
                os = documentReportService.generateDocumentByExcel(report, templateDtl, formParams);
                break;
            case RTF:
                os = documentReportService.generateDocumentByRtf(report, templateDtl, outputType, formParams);
                break;
            case DOC:
                os = documentReportService.generateDocumentByDoc(report, templateDtl, outputType, formParams);
                break;
            case HTML:
                os = documentReportService.generateDocumentByHtml(report, templateDtl, outputType, formParams);
                break;
            default:
                break;
        }
        if (os != null) {
            return ((ByteArrayOutputStream) os).toByteArray();
        }
        return new byte[0];
    }

    @Override
    public TemplateDtl getTemplateByParams(Report report, final Map<?, ?> httpReqParamMap) {
        // templateCodeAndTenant 拼接方式  templateCode@tenantId
        String templateCodeAndTenant = StringUtils.EMPTY;
        String lang = StringUtils.EMPTY;
        if (httpReqParamMap.containsKey(HrptConstants.FixedParam.TEMPLATE_CODE)) {
            final String[] values = (String[]) httpReqParamMap.get(HrptConstants.FixedParam.TEMPLATE_CODE);
            templateCodeAndTenant = values[0];
            if (Objects.equals(templateCodeAndTenant, HrptConstants.NULL)) {
                templateCodeAndTenant = null;
            }
        }
        String templateCode = null;
        Long tenantId = report.getTenantId();
        if (StringUtils.isNotBlank(templateCodeAndTenant)) {
            String[] str = templateCodeAndTenant.split("@");
            if (str.length > 1) {
                templateCode = str[0];
                tenantId = Long.valueOf(str[1]);
            } else {
                // 兼容没有传拼接编码的方式
                templateCode = templateCodeAndTenant;
            }
        }
        if (httpReqParamMap.containsKey(HrptConstants.FixedParam.LANG)) {
            final String[] values = (String[]) httpReqParamMap.get(HrptConstants.FixedParam.LANG);
            lang = values[0];
            if (Objects.equals(lang, HrptConstants.NULL)) {
                lang = null;
            }
        }
        return documentReportService.getReportTemplate(report, templateCode, tenantId, lang);
    }

    @Override
    public void asyncExportReportFile(Report report, Map<String, Object> formParams) {
        // 当前租户
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long tenantId = userDetails.getTenantId();
        Long requestId = reportRequestService.initReportRequest(tenantId, report, formParams);
        Assert.notNull(requestId, HrptMessageConstants.ERROR_REPORT_GENERATE);
        ApplicationContextHelper.getContext().getBean(IReportGenerateService.class).asyncGenerateTable(tenantId, requestId, report, formParams, TokenUtils.getToken(), userDetails);
    }

    /**
     * 异步执行报表
     */
    @Async("commonAsyncTaskExecutor")
    @Override
    public void asyncGenerateTable(Long tenantId, Long requestId, Report report, Map<String, Object> formParams, String token, CustomUserDetails userDetails) {
        DetailsHelper.setCustomUserDetails(userDetails);
        // 跨线程获取token，只能传递，直接拷贝RequestAttribute会造成后续请求参数丢失
        CustomTokenUtils.setToken(token);
        try {
            generateTable(tenantId, requestId, report, formParams);
        } finally {
            // 清除token
            CustomTokenUtils.clear();
        }
    }

    @Override
    public String generateTable(Long tenantId, Long requestId, Report report, Map<String, Object> formParams) {
        String fileUrl = null;
        try {
            String requestMessage = StringUtils.EMPTY;
            String requestStatus = HrptConstants.RequestStatus.STATUS_F;
            // 除了API类型，强制分页处理
            if (HrptConstants.DataSetType.TYPE_A.equals(report.getReportTypeCode())) {
                report.setPageFlag(BaseConstants.Flag.YES);
            }
            // 设置每页数据量
            formParams.put(HrptConstants.FixedParam.SIZE, reportConfig.getRequest().getPageSize());
            formParams.put(HrptConstants.FixedParam.PAGE, 0);
            // 预请求一次，判断数据集的返回
            List<MetaDataRow> perData = generateTableRows(report, formParams);
            if (perData.get(0) != null && StringUtils.isNotBlank(perData.get(0).getAsyncReportUuid())) {
                // 启动线程执行消费
                long start = System.currentTimeMillis();
                while (true) {
                    List<String> messageList = redisQueueHelper.pullAll(perData.get(0).getAsyncReportUuid());
                    if (CollectionUtils.isNotEmpty(messageList)) {
                        String result = messageList.get(0);
                        if (result.startsWith("http")) {
                            fileUrl = result;
                        }
                        break;
                    }
                    if (System.currentTimeMillis() - start > reportConfig.getRequest().getMaxWaitTime()) {
                        throw new CommonException(HrptMessageConstants.ASYNC_TIMEOUT);
                    }
                    // 等待30秒
                    Thread.sleep(reportConfig.getRequest().getInterval() * 1000L);
                }
            } else {
                // 构建excel
                fileUrl = generateExcel(tenantId, report, formParams, perData);
            }
            if (StringUtils.isBlank(fileUrl)) {
                requestStatus = HrptConstants.RequestStatus.STATUS_W;
                requestMessage = MessageAccessor.getMessage(HrptMessageConstants.ERROR_ASYNC_REPORT_REQUEST).desc();
            }
            reportRequestService.finishReportRequest(requestId, requestStatus, fileUrl, requestMessage);
        } catch (Exception e) {
            logger.error("Async execute report error!", e);
            reportRequestService.finishReportRequest(requestId, HrptConstants.RequestStatus.STATUS_E, null, StringUtils.substring(e.getMessage(), 0, 240));
        }
        return fileUrl;
    }

    private String generateExcel(Long tenantId, Report report, Map<String, Object> formParams, List<MetaDataRow> perData) throws Exception {
        // 报表列名称
        List<MetaDataColumn> metaColumns = JSON.parseArray(report.getMetaColumns(), MetaDataColumn.class);
        metaColumns = metaColumns.stream().sorted(Comparator.comparing(MetaDataColumn::getOrdinal))
                .filter(item -> item.getHidden() == BaseConstants.Flag.NO).collect(Collectors.toList());
        // 生成excel
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SXSSFWorkbook workbook = new SXSSFWorkbook(reportConfig.getRequest().getCacheSize());
        SXSSFSheet sheet = workbook.createSheet();
        List<String> nameList = new ArrayList<>();
        // 表头
        SXSSFRow headRow = sheet.createRow(0);
        for (int i = 0; i < metaColumns.size(); i++) {
            MetaDataColumn column = metaColumns.get(i);
            headRow.createCell(i).setCellValue(column.getText());
            nameList.add(column.getName());
        }
        int page = 0;
        int line = 1;
        // 未开启分页标识的数据只请求一次
        if (BaseConstants.Flag.YES.equals(report.getPageFlag())) {
            // 报表行数据
            List<MetaDataRow> list;
            do {
                // 设置分页属性
                formParams.put(HrptConstants.FixedParam.PAGE, page);
                if (page == 0) {
                    list = perData;
                } else {
                    list = generateTableRows(report, formParams);
                }
                if (CollectionUtils.isEmpty(list)) {
                    break;
                }
                page++;
                // 表数据
                line = addData(list, sheet, line, nameList);
            } while (CollectionUtils.isNotEmpty(list));
        } else {
            line = addData(perData, sheet, line, nameList);
        }
        workbook.write(out);
        // 上传文件
        return fileClient.uploadFile(tenantId, HZeroService.Report.BUCKET_NAME,
                HrptConstants.REPORT_DIR, report.getReportName() + ".xlsx", FileType.Application.XLS, out.toByteArray());
    }

    private int addData(List<MetaDataRow> list, SXSSFSheet sheet, int line, List<String> nameList) {
        for (MetaDataRow metaDataRow : list) {
            SXSSFRow row = sheet.createRow(line);
            line++;
            Map<String, MetaDataCell> dataMap = metaDataRow.getCells();
            int rowIndex = 0;
            for (String name : nameList) {
                if (dataMap.containsKey(name)) {
                    Object value = dataMap.get(name).getValue();
                    row.createCell(rowIndex).setCellValue(value == null ? StringUtils.EMPTY : String.valueOf(value));
                } else {
                    row.createCell(rowIndex).setCellValue(StringUtils.EMPTY);
                }
                rowIndex++;
            }
        }
        return line;
    }

    /**
     * 根据下载类型处理HTML的样式
     */
    private String buildHtmlStyle(String html, String outputType) {
        StringBuilder styleHtml = new StringBuilder();
        switch (outputType) {
            case HrptConstants.DocumentData.OUTPUT_FORMAT_XLS:
                if (!Objects.equals(html, "<p><h1>" + MessageAccessor.getMessage(HrptMessageConstants.INFO_ASYNC_REPORT_REQUEST).desc() + "</h1></p>")) {
                    styleHtml.append(StringUtils.replaceFirst(html, "<table", "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\""));
                }
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_HTML:
                styleHtml.append("<meta charset=\"utf-8\">\r\n"
                        + "<style type=\"text/css\">\r\n" + "#hreport{\r\n" + "    width: 100%;\r\n"
                        + "    border-collapse: collapse;\r\n" + "    font-size: 12px;\r\n"
                        + "    text-align: left;\r\n" + "}\r\n" + "#hreport tr td,th{\r\n"
                        + "    border: 1px solid #e8e8e8;\r\n" + "    padding: 9px 8px;\r\n" + "}\r\n"
                        + "#hreport thead tr{\r\n" + "    Background-color: #f5f5f5;\r\n"
                        + "    height: 45px;\r\n" + "    font-weight: bold;\r\n" + "}\r\n"
                        + "#hreport tbody tr:hover{\r\n" + "    background-color: #F0F4FF;\r\n" + "}\r\n"
                        + "</style>");
                styleHtml.append(html);
                break;
            default:
                styleHtml.append(html);
                break;
        }
        return styleHtml.toString();
    }

}
