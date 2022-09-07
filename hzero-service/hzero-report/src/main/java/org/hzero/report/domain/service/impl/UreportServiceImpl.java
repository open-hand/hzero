package org.hzero.report.domain.service.impl;

import java.io.*;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.html.Tools;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.*;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.provider.report.file.FileReportProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.codehaus.jackson.map.ObjectMapper;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.core.util.Pair;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.service.IUreportService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.meta.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * ureport报表相关
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/04 19:51
 */
@Service
public class UreportServiceImpl implements IUreportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UreportServiceImpl.class);
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();


    protected VelocityEngine ve;

    private static final String TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ureport><cell expand=\"None\" name=\"A1\" row=\"1\" col=\"1\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"B1\" row=\"1\" col=\"2\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"C1\" row=\"1\" col=\"3\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"D1\" row=\"1\" col=\"4\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"A2\" row=\"2\" col=\"1\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"B2\" row=\"2\" col=\"2\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"C2\" row=\"2\" col=\"3\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"D2\" row=\"2\" col=\"4\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"A3\" row=\"3\" col=\"1\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"B3\" row=\"3\" col=\"2\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"C3\" row=\"3\" col=\"3\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><cell expand=\"None\" name=\"D3\" row=\"3\" col=\"4\"><cell-style font-size=\"10\" align=\"center\" valign=\"middle\"></cell-style><simple-value><![CDATA[]]></simple-value></cell><row row-number=\"1\" height=\"18\"/><row row-number=\"2\" height=\"18\"/><row row-number=\"3\" height=\"18\"/><column col-number=\"1\" width=\"80\"/><column col-number=\"2\" width=\"80\"/><column col-number=\"3\" width=\"80\"/><column col-number=\"4\" width=\"80\"/><paper type=\"A4\" left-margin=\"90\" right-margin=\"90\"\n" +
            "    top-margin=\"72\" bottom-margin=\"72\" paging-mode=\"fitpage\" fixrows=\"0\"\n" +
            "    width=\"595\" height=\"842\" orientation=\"portrait\" html-report-align=\"left\" bg-image=\"\" html-interval-refresh-value=\"0\" column-enabled=\"false\"></paper></ureport>";

    @Autowired
    public UreportServiceImpl() {
        ve = new VelocityEngine();
        ve.setProperty(Velocity.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new NullLogChute());
        ve.init();
    }

    @Override
    public ReportProvider getReportProvider() {
        return ApplicationContextHelper.getContext().getBean(FileReportProvider.class);
    }

    @Override
    public ExportManager getExportManager() {
        return ApplicationContextHelper.getContext().getBean(ExportManagerImpl.class);
    }

    @Override
    public void initReportFile(String reportCode) {
        getReportProvider().saveReport(FileReportProvider.PREFIX + reportCode, TEMPLATE);
    }

    @Override
    public void clearReportFile(String reportCode) {
        getReportProvider().deleteReport(FileReportProvider.PREFIX + reportCode);
    }

    @Override
    public void designer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getParameter("_u");
        if (StringUtils.isBlank(filename)) {
            throw new CommonException(HrptMessageConstants.ERROR_TEMPLATE_NOT_EXIST);
        }
        VelocityContext context = new VelocityContext();
        context.put("contextPath", request.getContextPath());
        context.put("reportUuid", filename);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        Template template = ve.getTemplate("ureport-html/designer.html", "utf-8");
        PrintWriter writer = response.getWriter();
        template.merge(context, writer);
        writer.close();
    }

    @Override
    public void previewReport(Report report, JSONObject data, HttpServletRequest request) {
        VelocityContext context = new VelocityContext();
        HtmlReport htmlReport;
        try {
            htmlReport = loadReport(request, report);
        } catch (Exception ex) {
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_GENERATE, ex);
        }
        // 报表标题
        String title = report.getReportName();
        context.put("title", title);
        data.put("totalPage", htmlReport.getTotalPage());
        data.put("pageIndex", htmlReport.getPageIndex() - 1);
        // 强制不显示默认表单
        context.put("searchFormJs", "");
        context.put("downSearchFormHtml", "");
        context.put("upSearchFormHtml", "");

        context.put("content", htmlReport.getContent());
        context.put("style", htmlReport.getStyle());
        context.put("reportAlign", htmlReport.getReportAlign());
        context.put("totalPage", htmlReport.getTotalPage());
        context.put("totalPageWithCol", htmlReport.getTotalPageWithCol());
        context.put("pageIndex", htmlReport.getPageIndex());
        context.put("chartDatas", convertJson(htmlReport.getChartDatas()));
        context.put("error", false);
        context.put("file", FileReportProvider.PREFIX + report.getReportCode());
        context.put("intervalRefreshValue", htmlReport.getHtmlIntervalRefreshValue());
        String customParameters = buildCustomParameters(request);
        context.put("customParameters", customParameters);
        Tools tools;
        // 强制不显示工具
        tools = new Tools(false);
        tools.setShow(false);
        context.put("_t", "0");
        context.put("hasTools", true);
        context.put("tools", tools);
        context.put("contextPath", request.getContextPath());
        Template template = ve.getTemplate("ureport-html/html-preview.html", BaseConstants.DEFAULT_CHARSET);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        data.put(HrptConstants.HTML_TABLE, writer.toString());
    }

    @Override
    public void exportReport(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uReportFilename = FileReportProvider.PREFIX + report.getReportCode();
        Map<String, Object> params = buildParameters(request);
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
        try (OutputStream out = response.getOutputStream()) {
            ExportConfigure configure = new ExportConfigureImpl(uReportFilename, params, out);
            switch (outputType) {
                case HrptConstants.DocumentData.OUTPUT_FORMAT_XLS:
                    response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, report.getReportName() + ".xls"));
                    response.setContentType("application/vnd.ms-excel;");
                    getExportManager().exportExcel97(configure);
                    break;
                case HrptConstants.DocumentData.OUTPUT_FORMAT_XLSX:
                    response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, report.getReportName() + ".xlsx"));
                    response.setContentType("application/vnd.ms-excel;");
                    getExportManager().exportExcel(configure);
                    break;
                case HrptConstants.DocumentData.OUTPUT_FORMAT_DOCX:
                    response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, report.getReportName() + ".docx"));
                    response.setContentType("application/msword;");
                    getExportManager().exportWord(configure);
                    break;
                case HrptConstants.DocumentData.OUTPUT_FORMAT_PDF:
                    response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, report.getReportName() + ".pdf"));
                    response.setContentType("application/pdf;");
                    getExportManager().exportPdf(configure);
                    break;
                case HrptConstants.DocumentData.ONLINE_PRINT:
                    response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, report.getReportName() + ".pdf"));
                    response.setContentType("application/pdf;");
                    getExportManager().exportPdf(configure);
                    break;
                default:
                    return;
            }
            out.flush();
        }
    }

    @Override
    public List<FormElement> getForm(Report report) {
        List<FormElement> list = new ArrayList<>();
        Map<String, Pair<String, String>> valueMap;

        InputStream inputStream = getReportProvider().loadReport(report.getReportCode());
        try {
            DocumentBuilder builder = FACTORY.newDocumentBuilder();
            Document d = builder.parse(inputStream);
            valueMap = getDefaultValue(d.getElementsByTagName("ureport"));
            getForm(report.getReportCode(), d.getElementsByTagName("ureport"), list, valueMap);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
        return list;
    }

    private HtmlReport loadReport(HttpServletRequest request, Report report) {
        Map<String, Object> parameters = buildParameters(request);
        HtmlReport htmlReport;
        String uReportFilename = FileReportProvider.PREFIX + report.getReportCode();
        if (StringUtils.isBlank(uReportFilename)) {
            throw new ReportComputeException("Report file can not be null.");
        }
        if (BaseConstants.Flag.YES.equals(report.getPageFlag())) {
            String page = request.getParameter(HrptConstants.FixedParam.PAGE);
            int pageIndex = 1;
            if (StringUtils.isNotBlank(page)) {
                pageIndex = Integer.parseInt(page) + 1;
            }
            htmlReport = getExportManager().exportHtml(uReportFilename, request.getContextPath(), parameters, pageIndex);
        } else {
            htmlReport = getExportManager().exportHtml(uReportFilename, request.getContextPath(), parameters);
        }
        return htmlReport;
    }

    private String convertJson(Collection<ChartData> data) {
        if (CollectionUtils.isEmpty(data)) {
            return "";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new ReportComputeException(e);
        }
    }

    private String buildCustomParameters(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj == null) {
                continue;
            }
            String name = obj.toString();
            String value = request.getParameter(name);
            if (name == null || value == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }

    private String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            value = URLDecoder.decode(value, BaseConstants.DEFAULT_CHARSET);
            value = URLDecoder.decode(value, BaseConstants.DEFAULT_CHARSET);
            return value;
        } catch (Exception ex) {
            return value;
        }
    }

    private Map<String, Object> buildParameters(HttpServletRequest request) {
        Map<String, Object> parameters = new HashMap<>(16);
        Enumeration<?> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj == null) {
                continue;
            }
            String name = obj.toString();
            String value = request.getParameter(name);
            if (name == null || value == null || name.startsWith("_")) {
                continue;
            }
            parameters.put(name, decode(value));
        }
        // 插入权限数据参数
        setPermissionParams(parameters);
        return parameters;
    }

    /**
     * 设置数据权限参数
     */
    @SuppressWarnings("unchecked")
    private static void setPermissionParams(Map<String, Object> formParams) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails != null) {
            try {
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.USER_ID), FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.USER_ID, true));
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.LANGUAGE), FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.LANGUAGE, true));
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.ORGANIZATION_ID), FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.ORGANIZATION_ID, true));
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.ROLE_ID), FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.ROLE_ID, true));
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.TENANT_ID), FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.TENANT_ID, true));
                List<Long> roleIds = (List<Long>) FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.ROLE_IDS, true);
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.ROLE_IDS), StringUtils.join(roleIds, ","));
                List<Long> tenantIds = (List<Long>) FieldUtils.readDeclaredField(customUserDetails, HrptConstants.PermissionParam.TENANT_IDS, true);
                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.TENANT_IDS), StringUtils.join(tenantIds, ","));

                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.DATE), LocalDate.now());
            } catch (IllegalAccessException e) {
                LOGGER.error("CustomUserDetails Parser Error", e);
            }
        } else {
            LOGGER.error("CustomUserDetails Is Null");
        }
    }

    /**
     * 获取前线名称参数
     */
    private static String getPermissionParamName(String paramName) {
        return "u_" + paramName;
    }

    /**
     * 获取数据集参数及默认值
     */
    private static Map<String, Pair<String, String>> getDefaultValue(NodeList list) {
        Map<String, Pair<String, String>> map = new HashMap<>(16);
        Node parent = list.item(0);
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            String nodeName = node.getNodeName();
            if (!"datasource".equals(nodeName)) {
                continue;
            }
            // 数据源
            NodeList nodeChild = node.getChildNodes();
            for (int j = 0; j < nodeChild.getLength(); j++) {
                Node dataset = nodeChild.item(j);
                if (!"dataset".equals(dataset.getNodeName())) {
                    continue;
                }
                NodeList datasetChild = dataset.getChildNodes();
                for (int m = 0; m < datasetChild.getLength(); m++) {
                    Node parameter = datasetChild.item(m);
                    if (!"parameter".equals(parameter.getNodeName())) {
                        continue;
                    }
                    NamedNodeMap parameterMap = parameter.getAttributes();
                    String name = parameterMap.getNamedItem("name").getTextContent();
                    String dataType = parameterMap.getNamedItem("type").getTextContent();
                    String defaultValue = parameterMap.getNamedItem("default-value").getTextContent();
                    String type;
                    switch (dataType) {
                        case "Integer":
                            type = "integer";
                            break;
                        case "Float":
                        case "Boolean":
                            type = "float";
                            break;
                        case "Date":
                            type = "date";
                            break;
                        case "List":
                        case "String":
                        default:
                            type = "string";
                            break;

                    }
                    map.put(name, Pair.of(type, defaultValue == null ? StringUtils.EMPTY : defaultValue));
                }
            }
        }
        return map;
    }

    /**
     * 获取查询表单
     */
    private static void getForm(String reportCode, NodeList list, List<FormElement> formElements, Map<String, Pair<String, String>> valueMap) {
        Node parent = list.item(0);
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            String nodeName = node.getNodeName();
            if ("search-form".equals(nodeName)) {
                // 查询表单
                getParam(reportCode, node.getChildNodes(), formElements, valueMap);
            }
        }
    }

    /**
     * 获取查询表单配置
     */
    private static void getParam(String reportCode, NodeList nodeChild, List<FormElement> formElements, Map<String, Pair<String, String>> valueMap) {
        for (int i = 0; i < nodeChild.getLength(); i++) {
            Node item = nodeChild.item(i);
            String name = item.getNodeName();
            if ("input-text".equals(name)) {
                // 文本框
                NamedNodeMap parameterMap = item.getAttributes();
                // 展示名称
                String label = parameterMap.getNamedItem("label").getTextContent();
                // 参数字段
                String param = parameterMap.getNamedItem("bind-parameter").getTextContent();
                // 参数类型及默认值
                Pair<String, String> pair = valueMap.getOrDefault(param, Pair.of("string", StringUtils.EMPTY));
                TextBox box = new TextBox(param, label, pair.getSecond());
                box.setDataType(pair.getFirst());
                box.setIsRequired(BaseConstants.Flag.NO);
                box.setDefaultText(pair.getSecond());
                box.setDefaultValue(pair.getSecond());
                formElements.add(box);
            } else if ("input-datetime".equals(name)) {
                // 日期
                NamedNodeMap parameterMap = item.getAttributes();
                // 展示名称
                String label = parameterMap.getNamedItem("label").getTextContent();
                // 参数字段
                String param = parameterMap.getNamedItem("bind-parameter").getTextContent();
                // 参数类型及默认值
                Pair<String, String> pair = valueMap.getOrDefault(param, Pair.of("string", StringUtils.EMPTY));
                String format = parameterMap.getNamedItem("format").getTextContent();
                FormElement formElement;
                if ("yyyy-mm-dd".equals(format)) {
                    // 日期框
                    formElement = new DateBox(param, label, pair.getSecond());
                } else {
                    // 日期时间框
                    formElement = new DatetimeBox(param, label, pair.getSecond());
                }
                formElement.setDataType(pair.getFirst());
                formElement.setIsRequired(BaseConstants.Flag.NO);
                formElement.setDefaultText(pair.getSecond());
                formElement.setDefaultValue(pair.getSecond());
                formElements.add(formElement);
            } else if ("input-radio".equals(name)) {
                // 单选框
                NamedNodeMap parameterMap = item.getAttributes();
                // 展示名称
                String label = parameterMap.getNamedItem("label").getTextContent();
                // 参数字段
                String param = parameterMap.getNamedItem("bind-parameter").getTextContent();
                // 参数类型及默认值
                Pair<String, String> pair = valueMap.getOrDefault(param, Pair.of("string", StringUtils.EMPTY));
                List<SelectOption> options = getOptional(item.getChildNodes());
                RadioBox box = new RadioBox(param, label, options);
                box.setDataType(pair.getFirst());
                box.setIsRequired(BaseConstants.Flag.NO);
                box.setDefaultText(getDefaultText(options, pair.getSecond()));
                box.setDefaultValue(pair.getSecond());
                formElements.add(box);
            } else if ("input-checkbox".equals(name)) {
                // 复选框
                NamedNodeMap parameterMap = item.getAttributes();
                // 参数字段
                String param = parameterMap.getNamedItem("bind-parameter").getTextContent();
                // 展示名称
                String label = parameterMap.getNamedItem("label").getTextContent();
                // 参数类型及默认值
                Pair<String, String> pair = valueMap.getOrDefault(param, Pair.of("string", StringUtils.EMPTY));
                List<SelectOption> options = getOptional(item.getChildNodes());
                CheckBox box = new CheckBox(param, label, options);
                box.setDataType(pair.getFirst());
                box.setIsRequired(BaseConstants.Flag.NO);
                box.setDefaultText(getDefaultText(options, pair.getSecond()));
                box.setDefaultValue(pair.getSecond());
                formElements.add(box);
            } else if ("input-select".equals(name)) {
                // 单选列表
                NamedNodeMap parameterMap = item.getAttributes();
                // 展示名称
                String label = parameterMap.getNamedItem("label").getTextContent();
                // 参数字段
                String param = parameterMap.getNamedItem("bind-parameter").getTextContent();
                // 参数类型及默认值
                Pair<String, String> pair = valueMap.getOrDefault(param, Pair.of("string", StringUtils.EMPTY));
                List<SelectOption> selectOptions = new ArrayList<>();
                if (parameterMap.getNamedItem("use-dataset") == null || !"true".equals(parameterMap.getNamedItem("use-dataset").getTextContent())) {
                    selectOptions = getOptional(item.getChildNodes());
                } else {
                    String datasetName = parameterMap.getNamedItem("dataset").getTextContent();
                    String valueField = parameterMap.getNamedItem("value-field").getTextContent();
                    String displayField = parameterMap.getNamedItem("label-field").getTextContent();

                    Map<String, Object> userParams = new HashMap<>(16);
                    // 传入用户参数
                    setPermissionParams(userParams);
                    Dataset dataset = getDataSet(reportCode, datasetName, userParams);
                    for (Object obj : dataset.getData()) {
                        Object mean = Utils.getProperty(obj, displayField);
                        Object value = Utils.getProperty(obj, valueField);
                        selectOptions.add(new SelectOption(value == null ? "" : value.toString(), mean == null ? "" : mean.toString()));
                    }
                }
                ComboBox comboBox = new ComboBox(param, label, selectOptions);
                comboBox.setDataType(pair.getFirst());
                comboBox.setIsRequired(BaseConstants.Flag.NO);
                comboBox.setMultipled(false);
                comboBox.setDefaultText(getDefaultText(selectOptions, pair.getSecond()));
                comboBox.setDefaultValue(pair.getSecond());
                formElements.add(comboBox);
            } else {
                // 其他类型，继续遍历
                NodeList list = item.getChildNodes();
                if (list.getLength() > 0) {
                    getParam(reportCode, list, formElements, valueMap);
                }
            }
        }
    }

    /**
     * 获取单选、复选、下拉的选项值
     */
    private static List<SelectOption> getOptional(NodeList list) {
        List<SelectOption> selectOptions = new ArrayList<>();
        for (int j = 0; j < list.getLength(); j++) {
            Node option = list.item(j);
            if ("option".equals(option.getNodeName())) {
                NamedNodeMap optionMap = option.getAttributes();
                String mean = optionMap.getNamedItem("label").getTextContent();
                String value = optionMap.getNamedItem("value").getTextContent();
                selectOptions.add(new SelectOption(value, mean));
            }
        }
        return selectOptions;
    }

    /**
     * 获取默认显示值
     */
    private static String getDefaultText(List<SelectOption> options, String defaultValue) {
        for (SelectOption item : options) {
            if (Objects.equals(defaultValue, item.getValue())) {
                return item.getMeaning();
            }
        }
        return defaultValue;
    }

    /**
     * 获取ureport数据集
     *
     * @param reportCode  报表定义编码
     * @param datasetName ureport数据集名称
     * @param parameters  参数
     * @return 数据集
     */
    private static Dataset getDataSet(String reportCode, String datasetName, Map<String, Object> parameters) {
        ReportRender reportRender = ApplicationContextHelper.getContext().getBean(ReportRender.class);
        ReportDefinition reportDefinition = reportRender.getReportDefinition(FileReportProvider.PREFIX + reportCode);
        com.bstek.ureport.model.Report report = reportRender.render(reportDefinition, parameters);
        Map<String, Dataset> map = report.getContext().getDatasetMap();
        return map.getOrDefault(datasetName, null);
    }
}