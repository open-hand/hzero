package org.hzero.report.domain.service.impl;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.platform.ds.DatasourceHelper;
import org.hzero.boot.platform.ds.constant.DsConstants;
import org.hzero.boot.platform.ds.vo.DatasourceVO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.jdbc.constant.DBPoolTypeEnum;
import org.hzero.jdbc.constant.DatabaseTypeEnum;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.mybatis.common.Criteria;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.engine.query.QueryerFactory;
import org.hzero.report.infra.enums.ReportTypeEnum;
import org.hzero.report.infra.meta.form.*;
import org.hzero.report.infra.meta.option.QueryParameterOptions;
import org.hzero.report.infra.meta.option.ReportOptions;
import org.hzero.report.infra.util.VelocityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 报表元数据服务实现类
 *
 * @author xianzhi.chen@hand-china.com 2018年12月5日上午10:09:15
 */
@Service
public class ReportMetaServiceImpl implements IReportMetaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 参数提取正则表达式
     */
    private static final String PARAMETER_REGEX = "\\$\\{[a-zA-Z]+\\}";

    private final DatasourceHelper datasourceHelper;
    private final DatasetRepository datasetRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public ReportMetaServiceImpl(DatasourceHelper datasourceHelper,
                                 DatasetRepository datasetRepository,
                                 ReportRepository reportRepository) {
        this.datasourceHelper = datasourceHelper;
        this.datasetRepository = datasetRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public ReportDataSource getReportDataSource(Long tenantId, String datasourceCode) {
        if (StringUtils.isBlank(datasourceCode)) {
            return null;
        }
        DatasourceVO ds = datasourceHelper.getDatasource(DsConstants.DsPurpose.DR, tenantId, datasourceCode);
        Assert.notNull(ds, HrptMessageConstants.ERROR_NOFUND_DATASOURCE);
        if (logger.isDebugEnabled()) {
            logger.debug("datasource info : {}", ds);
        }
        // 设置查询类和连接池类
        ds.setQueryerClass(String.format(QueryConstants.Datasource.QUERYER_TEMPLATE, DatabaseTypeEnum.valueOf2(ds.getDbType()).getValue()));
        ds.setPoolClass(String.format(QueryConstants.Datasource.DBPOOL_TEMPLATE, DBPoolTypeEnum.valueOf2(ds.getDbPoolType()).getValue()));

        Map<String, Object> options = new HashMap<>(3);
        if (StringUtils.isNotEmpty(ds.getOptions())) {
            options = JSON.parseObject(ds.getOptions());
        }
        return new ReportDataSource(tenantId, datasourceCode, ds.getDriverClass(), ds.getDatasourceUrl(), ds.getUsername(), ds.getPasswordEncrypted(), ds.getQueryerClass(), ds.getPoolClass(), options);
    }

    @Override
    public Report getReportById(long reportId) {
        Report record = new Report();
        record.setReportId(reportId);
        return reportRepository.selectOneOptional(record,
                new Criteria().select(Report.FIELD_REPORT_ID, Report.FIELD_REPORT_UUID, Report.FIELD_DATASET_ID,
                        Report.FIELD_META_COLUMNS, Report.FIELD_OPTIONS, Report.FIELD_REPORT_CODE,
                        Report.FIELD_REPORT_NAME, Report.FIELD_REPORT_TYPE_CODE,
                        Report.FIELD_TEMPLATE_TYPE_CODE, Report.FIELD_TENANT_ID, Report.FIELD_REMARK));
    }

    @Override
    public Report getReportByKey(String reportKey) {
        Long tenantId = DetailsHelper.getUserDetails().getTenantId();
        Report report = reportRepository.selectReport(reportKey, tenantId);
        if (report == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            report = reportRepository.selectReport(reportKey, BaseConstants.DEFAULT_TENANT_ID);
        }
        return report;
    }

    @Override
    public Report getReportIgnorePermission(Long tenantId, String reportKey) {
        Report report = reportRepository.selectReportIgnorePermission(tenantId, reportKey);
        if (report == null && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            report = reportRepository.selectReportIgnorePermission(BaseConstants.DEFAULT_TENANT_ID, reportKey);
        }
        return report;
    }

    @Override
    public ReportOptions parseReportOptions(String json) {
        if (StringUtils.isBlank(json)) {
            // 默认横向
            return ReportOptions.builder().layout(HrptConstants.LayoutType.HORIZONTAL).statColumnLayout(HrptConstants.LayoutType.HORIZONTAL).build();
        }
        return JSON.parseObject(json, ReportOptions.class);
    }

    @Override
    public ReportParameter createSimpleReportParameter(Report report, Map<String, Object> formParams) {
        String sqlText = new ReportSqlTemplate(report.getSqlText(), formParams).execute();
        Set<String> enabledStatColumn = this.getEnabledStatColumns(formParams);
        List<MetaDataColumn> metaColumns = this.parseMetaColumns(report.getMetaColumns());
        // 构建SQL分页参数
        SqlPageInfo sqlPageInfo = this.getSqlPageInfo(report, formParams);
        return new ReportParameter(report.getReportId(), report.getReportCode(), report.getReportName(), metaColumns, enabledStatColumn,
                Boolean.parseBoolean(formParams.get(HrptConstants.FixedParam.IS_ROW_SPAN).toString()), sqlText, sqlPageInfo);
    }

    @Override
    public ReportParameter createReportParameter(Report report, Map<String, Object> formParams) {
        String sqlText = new ReportSqlTemplate(report.getSqlText(), formParams).execute();
        Set<String> enabledStatColumn = this.getEnabledStatColumns(formParams);
        ReportOptions options = this.parseReportOptions(report.getOptions());
        List<MetaDataColumn> metaColumns = this.parseMetaColumns(report.getMetaColumns());
        // 构建SQL分页参数
        SqlPageInfo sqlPageInfo = this.getSqlPageInfo(report, formParams);
        return new ReportParameter(report.getReportId(), report.getReportCode(), report.getReportName(), options.getLayout(), options.getStatColumnLayout(),
                metaColumns, enabledStatColumn, Boolean.parseBoolean(formParams.get(HrptConstants.FixedParam.IS_ROW_SPAN).toString()), sqlText, sqlPageInfo);
    }

    @Override
    public List<MetaDataColumn> parseMetaColumns(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(json, MetaDataColumn.class);
    }

    @Override
    public List<MetaDataColumn> getMetaDataColumns(Long tenantId, String datasourceCode, String sqlText) {
        final ReportDataSource reportDataSource = this.getReportDataSource(tenantId, datasourceCode);
        return QueryerFactory.create(reportDataSource).parseMetaDataColumns(sqlText);
    }

    @Override
    public List<QueryParameterOptions> getQueryParameters(String sqlText) {
        List<QueryParameterOptions> queryParameterList = new ArrayList<>();
        Set<String> paramList = Regexs.matchString(PARAMETER_REGEX, sqlText);
        Iterator<String> it = paramList.iterator();
        for (int i = 1, size = paramList.size(); i <= size; i++) {
            String queryParam = it.next();
            // 过滤掉权限参数
            if (queryParam.startsWith(HrptConstants.PermissionParam.PREFIX)) {
                continue;
            }
            QueryParameterOptions queryParameterOption = new QueryParameterOptions();
            queryParameterOption.setOrdinal(i);
            queryParameterOption.setName(queryParam.substring(2, queryParam.length() - 1));
            queryParameterOption.setText(queryParameterOption.getName());
            queryParameterOption.setFormElement("text");
            queryParameterList.add(queryParameterOption);
        }
        return queryParameterList;
    }

    @Override
    public List<FormElement> getQueryParamFormElements(Long datasetId, Map<String, Object> buildInParams) {
        Dataset dataset = datasetRepository.selectByPrimaryKey(datasetId);
        if (dataset == null) {
            return Collections.emptyList();
        }
        List<QueryParameterOptions> queryParams = this.parseQueryParams(dataset.getQueryParams());
        // 排序
        queryParams = queryParams.stream().sorted(Comparator.comparing(QueryParameterOptions::getOrdinal)).collect(Collectors.toList());
        List<FormElement> formElements = new ArrayList<>(3);
        for (QueryParameterOptions queryParam : queryParams) {
            FormElement formElement = null;
            queryParam.setDefaultMeaning(VelocityUtils.parse(queryParam.getDefaultMeaning(), buildInParams));
            queryParam.setDefaultValue(VelocityUtils.parse(queryParam.getDefaultValue(), buildInParams));
            queryParam.setContent(VelocityUtils.parse(queryParam.getContent(), buildInParams));
            String element = queryParam.getFormElement().toLowerCase();
            // 单选、多选下拉框 select
            if (HrptConstants.ParamFormElement.SELECT.equals(element)
                    || HrptConstants.ParamFormElement.SELECT_MUL.equalsIgnoreCase(element)) {
                formElement = this.getComboBoxFormElements(queryParam, dataset.getTenantId(), dataset.getDatasourceCode());
                // 单选框 Radiobox
            } else if (HrptConstants.ParamFormElement.RADIO.equals(element)) {
                formElement = this.getRadioBoxFormElements(queryParam, dataset.getTenantId(), dataset.getDatasourceCode());
                // 复选框 Checkbox
            } else if (HrptConstants.ParamFormElement.CHECKBOX.equals(element)) {
                formElement = this.getCheckBoxFormElements(queryParam, dataset.getTenantId(), dataset.getDatasourceCode());
                // 文本框 Input
            } else if (HrptConstants.ParamFormElement.TEXT.equals(element)) {
                formElement = new TextBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue());
                // 多值文本框 Input
            } else if (HrptConstants.ParamFormElement.TEXT_MUL.equalsIgnoreCase(element)) {
                formElement = new TextMulBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue());
                // 日期 DatePicker
            } else if (HrptConstants.ParamFormElement.DATE.equals(element)) {
                formElement = new DateBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue());
                // 日期时间 DatetimePicker
            } else if (HrptConstants.ParamFormElement.DATE_TIME.equals(element)) {
                formElement = new DatetimeBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue());
                // 数字框 InputNumber
            } else if (HrptConstants.ParamFormElement.INPUT_NUMBER.equals(element)) {
                formElement = new NumberBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue());
                // 值列表 Lov
            } else if (HrptConstants.ParamFormElement.LOV.equals(element)) {
                formElement = new LovBox(queryParam.getName(), queryParam.getText(), queryParam.getDefaultValue(), queryParam.getContent());
            }
            if (formElement != null) {
                this.setElementCommonProperities(queryParam, formElement);
                formElements.add(formElement);
            }
        }
        return formElements;
    }

    @Override
    public Map<String, Object> getBuildInParameters(Map<?, ?> paramMap) {
        Map<String, Object> formParams = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        this.setBuildInParams(formParams, paramMap);
        this.setPermissionParams(formParams);
        return formParams;
    }

    @Override
    public Map<String, Object> getFormParameters(Long datasetId, Map<?, ?> paramMap) {
        Map<String, Object> formParams = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        this.setBuildInParams(formParams, paramMap);
        this.setQueryParams(datasetId, formParams, paramMap);
        this.setPermissionParams(formParams);
        return formParams;
    }

    @Override
    public List<QueryParameterOptions> parseQueryParams(String json) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>(0);
        }
        return JSON.parseArray(json, QueryParameterOptions.class);
    }

    @Override
    public String getSqlText(String sqlText, String queryParams, HttpServletRequest request) {
        Map<String, Object> formParameters = this.getBuildInParameters(request.getParameterMap());
        if (StringUtils.isNotBlank(queryParams)) {
            List<QueryParameterOptions> queryParameters = JSON.parseArray(queryParams, QueryParameterOptions.class);
            queryParameters.stream().filter(parameter -> !formParameters.containsKey(parameter.getName()))
                    .forEach(parameter -> formParameters.put(parameter.getName(), parameter.getDefaultValue()));
        }
        return new ReportSqlTemplate(sqlText, formParameters).execute();
    }

    @Override
    public Long getMetaDataCount(Long tenantId, String datasourceCode, String sqlText) {
        final ReportDataSource reportDataSource = this.getReportDataSource(tenantId, datasourceCode);
        return QueryerFactory.create(reportDataSource).getMetaDataCount(sqlText);
    }

    @Override
    public String getExecuteSqlText(String sqlText, Map<String, Object> formParams) {
        return new ReportSqlTemplate(sqlText, formParams).execute();
    }

    @Override
    public boolean isAsyncExecute(Long tenantId, String datasourceCode, String sqlText, Map<String, Object> formParams,
                                  Long thresholdValue, Integer asyncFlag) {
        if (Objects.equals(asyncFlag, BaseConstants.Flag.YES)) {
            return true;
        }
        // 获取执行SQL
        String executeSql = getExecuteSqlText(sqlText, formParams);
        // 获取总条数
        Long cnt = getMetaDataCount(tenantId, datasourceCode, executeSql);
        // 与报表异步阈值进行判断，如为空则使用默认值
        return cnt >= (thresholdValue == null ? HrptConstants.ASYNC_THRESHOLD_VALUE : thresholdValue);
    }

    private void setBuildInParams(Map<String, Object> formParams, Map<?, ?> parameterMap) {
        // 判断是否设置报表统计列
        if (parameterMap.containsKey(HrptConstants.FixedParam.STAT_COLUMNS)) {
            String[] values = (String[]) parameterMap.get(HrptConstants.FixedParam.STAT_COLUMNS);
            formParams.put(HrptConstants.FixedParam.STAT_COLUMNS, StringUtils.join(values, ','));
        } else {
            formParams.put(HrptConstants.FixedParam.STAT_COLUMNS, "");
        }
        // 判断是否设置报表表格行跨行显示
        if (parameterMap.containsKey(HrptConstants.FixedParam.IS_ROW_SPAN)) {
            String[] values = (String[]) parameterMap.get(HrptConstants.FixedParam.IS_ROW_SPAN);
            formParams.put(HrptConstants.FixedParam.IS_ROW_SPAN, values[0]);
        } else {
            formParams.put(HrptConstants.FixedParam.IS_ROW_SPAN, "true");
        }
        // 判断是否设置分页参数
        if (parameterMap.containsKey(HrptConstants.FixedParam.PAGE)) {
            String[] values = (String[]) parameterMap.get(HrptConstants.FixedParam.PAGE);
            formParams.put(HrptConstants.FixedParam.PAGE, values[0]);
        }
        // 判断是否分页条数
        if (parameterMap.containsKey(HrptConstants.FixedParam.SIZE)) {
            String[] values = (String[]) parameterMap.get(HrptConstants.FixedParam.SIZE);
            formParams.put(HrptConstants.FixedParam.SIZE, values[0]);
        }
    }

    /**
     * 获取查询参数值
     */
    private String getQueryParamValue(String dataType, String[] values) {
        if (values.length == 1) {
            return values[0];
        }
        if (HrptConstants.ParamDataType.FLOAT.equals(dataType)
                || HrptConstants.ParamDataType.INTEGER.equals(dataType)) {
            return StringUtils.join(values, ",");
        }
        return StringUtils.join(values, "','");
    }

    /**
     * 设置查询参数
     */
    private void setQueryParams(Long datasetId, Map<String, Object> formParams, Map<?, ?> parameterMap) {
        String[] values;
        Dataset dataset = datasetRepository.selectByPrimaryKey(datasetId);
        String type = dataset.getSqlType();
        if (HrptConstants.DataSetType.TYPE_A.equals(type)) {
            // 插入数据集信息
            formParams.put(HrptConstants.DataSetParam.PREFIX + HrptConstants.DataSetParam.DATASET_CODE, dataset.getDatasetCode());
            formParams.put(HrptConstants.DataSetParam.PREFIX + HrptConstants.DataSetParam.DATASET_TENANT, dataset.getTenantId());
        }

        List<QueryParameterOptions> queryParams = this.parseQueryParams(dataset.getQueryParams());
        for (QueryParameterOptions queryParam : queryParams) {
            String value = "";
            values = (String[]) parameterMap.get(queryParam.getName());
            if (values != null && values.length > 0) {
                value = this.getQueryParamValue(queryParam.getDataType(), values);
            }
            formParams.put(queryParam.getName(), value);
        }
    }

    /**
     * 设置数据权限参数
     */
    @SuppressWarnings("unchecked")
    private void setPermissionParams(Map<String, Object> formParams) {
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

                formParams.put(getPermissionParamName(HrptConstants.PermissionParam.MYSQL_TIMEZONE), getMysqlTimezone(customUserDetails.getTimeZone()));
            } catch (IllegalAccessException e) {
                logger.error("CustomUserDetails Parser Error", e);
            }
        } else {
            logger.error("CustomUserDetails Is Null");
        }
    }

    /**
     * 获取mysql的时区表示
     *
     * @param timezone 时区
     * @return GMT+8 -> +8:00
     */
    private String getMysqlTimezone(String timezone) {
        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        return timeZone.getID().substring(3);
    }

    /**
     * 获取前线名称参数
     */
    private String getPermissionParamName(String paramName) {
        return HrptConstants.PermissionParam.PREFIX + paramName;
    }

    /**
     * 获取下拉框动态值
     */
    private ComboBox getComboBoxFormElements(QueryParameterOptions queryParam, Long tenantId, String datasourceCode) {
        List<ReportQueryParamItem> options = this.getParamOptions(queryParam, tenantId, datasourceCode);
        List<SelectOption> selectOptions = new ArrayList<>(options.size());
        for (ReportQueryParamItem option : options) {
            selectOptions.add(new SelectOption(option.getValue(), option.getMeaning()));
        }
        ComboBox comboBox = new ComboBox(queryParam.getName(), queryParam.getText(), selectOptions);
        comboBox.setMultipled(HrptConstants.ParamFormElement.SELECT_MUL.equals(queryParam.getFormElement()));
        return comboBox;
    }

    /**
     * 获取复选框值
     */
    private CheckBox getCheckBoxFormElements(QueryParameterOptions queryParam, Long tenantId, String datasourceCode) {
        List<ReportQueryParamItem> options = this.getParamOptions(queryParam, tenantId, datasourceCode);
        List<SelectOption> selectOptions = new ArrayList<>(options.size());
        for (ReportQueryParamItem option : options) {
            selectOptions.add(new SelectOption(option.getValue(), option.getMeaning()));
        }
        return new CheckBox(queryParam.getName(), queryParam.getText(), selectOptions);
    }

    /**
     * 获取单选框值
     */
    private RadioBox getRadioBoxFormElements(QueryParameterOptions queryParam, Long tenantId, String datasourceCode) {
        List<ReportQueryParamItem> options = this.getParamOptions(queryParam, tenantId, datasourceCode);
        List<SelectOption> selectOptions = new ArrayList<>(options.size());
        for (ReportQueryParamItem option : options) {
            selectOptions.add(new SelectOption(option.getValue(), option.getMeaning()));
        }
        return new RadioBox(queryParam.getName(), queryParam.getText(), selectOptions);
    }

    /**
     * 设置参数通用属性
     */
    private void setElementCommonProperities(QueryParameterOptions queryParam, FormElement formElement) {
        formElement.setDataType(queryParam.getDataType());
        formElement.setHeight(queryParam.getHeight());
        formElement.setWidth(queryParam.getWidth());
        formElement.setIsRequired(queryParam.getIsRequired());
        formElement.setDefaultText(queryParam.getDefaultMeaning());
        formElement.setDefaultValue(queryParam.getDefaultValue());
        formElement.setComment(queryParam.getComment());
    }

    /**
     * 获取参数动态内容配置
     */
    private List<ReportQueryParamItem> getParamOptions(QueryParameterOptions queryParam, Long tenantId, String datasourceCode) {
        if (HrptConstants.ParamDataSource.SQL.equals(queryParam.getDataSource())) {
            return this.executeQueryParamSqlText(tenantId, datasourceCode, queryParam.getContent());
        }
        List<ReportQueryParamItem> options = new ArrayList<>();
        if (HrptConstants.ParamDataSource.TEXT.equals(queryParam.getDataSource())
                && StringUtils.isNoneBlank(queryParam.getContent())) {
            HashSet<String> set = new HashSet<>();
            String[] optionSplits = StringUtils.split(queryParam.getContent(), '|');
            for (String option : optionSplits) {
                String[] valueMeanings = StringUtils.split(option, ',');
                String value = valueMeanings[0];
                String meaning = valueMeanings.length > 1 ? valueMeanings[1] : value;
                if (!set.contains(value)) {
                    set.add(value);
                    options.add(new ReportQueryParamItem(value, meaning));
                }
            }
        }
        return options;
    }

    /**
     * 执行报表参数SQL
     */
    private List<ReportQueryParamItem> executeQueryParamSqlText(Long tenantId, String datasourceCode, String sqlText) {
        final ReportDataSource reportDataSource = this.getReportDataSource(tenantId, datasourceCode);
        return QueryerFactory.create(reportDataSource).parseQueryParamItems(sqlText);
    }

    /**
     * 获取生效的统计列集合
     */
    private Set<String> getEnabledStatColumns(Map<String, Object> formParams) {
        Set<String> checkedSet = new HashSet<>();
        String checkedColumnNames = formParams.get(HrptConstants.FixedParam.STAT_COLUMNS).toString();
        if (StringUtils.isBlank(checkedColumnNames)) {
            return checkedSet;
        }
        String[] columnNames = StringUtils.split(checkedColumnNames, ',');
        checkedSet.addAll(Arrays.asList(columnNames));
        return checkedSet;
    }

    /**
     * 获取SQL分页参数信息
     */
    private SqlPageInfo getSqlPageInfo(final Report report, final Map<String, Object> formParams) {
        // 仅允许平面报表分页
        if (Objects.equals(BaseConstants.Flag.YES, report.getPageFlag())) {
            if (StringUtils.equals(ReportTypeEnum.TABLE.getValue(), report.getReportTypeCode()) ||
                    StringUtils.equals(ReportTypeEnum.SIMPLE_TABLE.getValue(), report.getReportTypeCode())) {
                int page = Integer.parseInt(String.valueOf(formParams.getOrDefault(HrptConstants.FixedParam.PAGE, BaseConstants.Digital.ZERO)));
                int size = Integer.parseInt(String.valueOf(formParams.getOrDefault(HrptConstants.FixedParam.SIZE, BaseConstants.PAGE_SIZE)));
                return new SqlPageInfo(page, size, true);
            }
        }
        return new SqlPageInfo();
    }

}
