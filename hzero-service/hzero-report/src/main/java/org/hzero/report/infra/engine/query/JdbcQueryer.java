package org.hzero.report.infra.engine.query;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.jdbc.QueryFactory;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.util.JdbcUtils;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.SqlXmlParser;
import org.hzero.report.infra.engine.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * 报表查询抽象类
 *
 * @author xianzhi.chen@hand-china.com 20 18年10月17日下午7:02:01
 */
public class JdbcQueryer implements Query {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected ReportDataSource dataSource;
    protected ReportParameter parameter;
    protected List<MetaDataColumn> metaDataColumns;
    private final org.hzero.jdbc.Query jdbcQuery;

    /**
     * 处理SQL
     */

    protected JdbcQueryer(ReportDataSource dataSource, ReportParameter parameter) {
        this.dataSource = dataSource;
        this.parameter = parameter;
        this.metaDataColumns = this.parameter == null ? new ArrayList<>(0) : new ArrayList<>(this.parameter.getMetaColumns());
        this.jdbcQuery = QueryFactory.create(dataSource);
    }

    /**
     * 获取元数据列信息
     *
     * @return List<MetaDataColumn>
     */
    @Override
    public List<MetaDataColumn> getMetaDataColumns() {
        return this.metaDataColumns;
    }

    /**
     * 解析元数据列
     *
     * @param sqlText sql
     * @return List<MetaDataColumn>
     */
    @Override
    public List<MetaDataColumn> parseMetaDataColumns(String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<MetaDataColumn> columns;
        try {
            this.logger.debug("Parse Report MetaDataColumns SQL:{}", sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(this.preProcessSqlText(sqlText));
            ResultSetMetaData rsMataData = rs.getMetaData();
            int count = rsMataData.getColumnCount();
            columns = new ArrayList<>(count);
            for (int i = 1; i <= count; i++) {
                MetaDataColumn column = new MetaDataColumn();
                column.setOrdinal(i);
                column.setName(rsMataData.getColumnLabel(i));
                column.setDataType(rsMataData.getColumnTypeName(i));
                column.setWidth(rsMataData.getColumnDisplaySize(i));
                columns.add(column);
            }
        } catch (SQLException ex) {
            throw new CommonException(HrptMessageConstants.SQL_EXCEPTION, ex.getMessage());
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, rs);
        }
        return columns;
    }

    /**
     * 解析查询参数项
     *
     * @param sqlText sql
     * @return List<ReportQueryParamItem>
     */
    @Override
    public List<ReportQueryParamItem> parseQueryParamItems(String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<ReportQueryParamItem> rows = new ArrayList<>();
        try {
            this.logger.debug(sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlText);
            while (rs.next()) {
                String value = rs.getString(ReportQueryParamItem.FIELD_VALUE);
                String meaning = rs.getString(ReportQueryParamItem.FIELD_MEANING);
                value = (value == null) ? "" : value.trim();
                meaning = (meaning == null) ? "" : meaning.trim();
                rows.add(new ReportQueryParamItem(value, meaning));
            }
        } catch (SQLException ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, rs);
        }
        return rows;
    }

    /**
     * 获取元数据行信息
     *
     * @return List<MetaDataRow>
     */
    @Override
    public List<MetaDataRow> getMetaDataRows() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.logger.debug(this.parameter.getSqlText());
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(this.prePageSqlText(this.parameter));
            return this.getMetaDataRows(rs, this.getSqlColumns(this.parameter.getMetaColumns()));
        } catch (Exception ex) {
            this.logger.error(String.format("SqlText:%s，Msg:%s", this.parameter.getSqlText(), ex));
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, rs);
        }
    }

    /**
     * 获取XML数据
     */
    @Override
    public Document getMetaDataXml(String sqlType, String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        try {
            this.logger.debug(sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement(QueryConstants.DataXmlAttr.DEFAULT_DS);
            // 判断是否有二维码路径字段
            ReportConfig reportConfig = ApplicationContextHelper.getContext().getBean(ReportConfig.class);
            if (StringUtils.isNotBlank(reportConfig.getQrCodeUrl())) {
                root.addElement(HrptConstants.QR_CODE_URL).addText(reportConfig.getQrCodeUrl());
            }
            if (StringUtils.equals(sqlType, HrptConstants.DataSetType.TYPE_C)) {
                SqlXmlParser sxp = new SqlXmlParser();
                SqlXmlLeaf sqlXmlLeaf = sxp.getSqlLeaf(sqlText);
                // 递归执行组装XML
                selectXmlDataByXmlSql(stmt, sqlXmlLeaf, root, null);
            } else {
                selectXmlDataBySql(stmt, sqlText, root);
            }
            return doc;
        } catch (Exception ex) {
            this.logger.error(String.format("SqlText:%s，Msg:%s", sqlText, ex));
            throw new CommonException(QueryConstants.Error.ERROR_PARAMETER_SETTING, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, null);
        }
    }

    /**
     * 获取Map数据
     */
    @Override
    public Map<String, Object> getMetaDataMap(String sqlType, String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        try {
            this.logger.debug(sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            Map<String, Object> resultMap = new HashMap<>(BaseConstants.Digital.SIXTEEN);
            // 判断是否有二维码路径字段
            ReportConfig reportConfig = ApplicationContextHelper.getContext().getBean(ReportConfig.class);
            if (StringUtils.isNotBlank(reportConfig.getQrCodeUrl())) {
                resultMap.put(HrptConstants.QR_CODE_URL, reportConfig.getQrCodeUrl());
            }
            if (StringUtils.equals(sqlType, HrptConstants.DataSetType.TYPE_C)) {
                SqlXmlParser sxp = new SqlXmlParser();
                SqlXmlLeaf sqlXmlLeaf = sxp.getSqlLeaf(sqlText);
                // 递归执行组装Map
                selectMapDataByXmlSql(stmt, sqlXmlLeaf, resultMap, null);
            } else {
                selectMapDataBySql(stmt, sqlText, resultMap);
            }
            return resultMap;
        } catch (Exception ex) {
            this.logger.error(String.format("SqlText:%s，Msg:%s", sqlText, ex));
            throw new CommonException(QueryConstants.Error.ERROR_PARAMETER_SETTING, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, null);
        }
    }

    /**
     * 获取元数据总条数
     *
     * @return 行条数
     */
    @Override
    public long getMetaDataCount() {
        return getMetaDataCount(this.parameter.getSqlText());
    }

    /**
     * 获取查询SQL执行的总条数
     */
    @Override
    public long getMetaDataCount(String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.logger.debug(sqlText);
            conn = this.getJdbcConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(this.getSmartCountSql(sqlText));
            rs.next();
            return rs.getLong(1);
        } catch (Exception ex) {
            this.logger.error(String.format("SqlText:%s，Msg:%s", sqlText, ex));
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, rs);
        }
    }


    /**
     * 获取元数据行
     */
    protected List<MetaDataRow> getMetaDataRows(ResultSet rs, List<MetaDataColumn> sqlColumns) throws SQLException {
        List<MetaDataRow> rows = new ArrayList<>();
        while (rs.next()) {
            MetaDataRow row = new MetaDataRow();
            for (MetaDataColumn column : sqlColumns) {
                // 隐藏的列不生成到path中
                if (column.getHidden() == BaseConstants.Flag.NO) {
                    Object value = rs.getObject(column.getName());
                    if (column.getDataType().contains("BINARY")) {
                        value = new String((byte[]) value, StandardCharsets.UTF_8);
                    }
                    if (value != null && StringUtils.isNotBlank(column.getFormat())) {
                        // 日期类型数据
                        if (value instanceof Timestamp) {
                            value = new SimpleDateFormat(column.getFormat()).format((Timestamp) value);
                        }
                        // 日期类型数据
                        if (value instanceof Date) {
                            value = new SimpleDateFormat(column.getFormat()).format((Date) value);
                        }
                        // Decimal类型数据
                        if (value instanceof BigDecimal) {
                            value = new DecimalFormat(column.getFormat()).format(value);
                        }
                    }
                    row.add(new MetaDataCell(column, column.getName(), value));
                }
            }
            rows.add(row);
        }
        return rows;
    }

    /**
     * 获取SQL列集合
     */
    protected List<MetaDataColumn> getSqlColumns(List<MetaDataColumn> metaDataColumns) {
        return metaDataColumns.stream().filter(x -> !HrptConstants.ColumnType.COMPUTED.equals(x.getType())).collect(Collectors.toList());
    }

    /**
     * 根据Xml结构SQL获取
     */
    protected void selectXmlDataByXmlSql(Statement stmt, SqlXmlLeaf leaf, Element resultData, Map<String, Object> paramMap) {
        // 执行本层级查询
        List<Map<String, Object>> thisResult = getMapDataBySql(stmt, leaf.getSql(), paramMap);
        leaf.setDatas(thisResult);
        // 如果查询数据不为空且当前节点拥有子节点，则将当前节点数据视为子节点查询参数，执行子查询
        if (CollectionUtils.isNotEmpty(leaf.getDatas())) {
            for (int i = 0, length = leaf.getDatas().size(); i < length; i++) {
                // 如Select查询属性为空，则不需要新增XML节点
                Element el = StringUtils.isNotBlank(leaf.getName()) ? resultData.addElement(leaf.getName()) : resultData;
                for (Map.Entry<String, Object> entry : leaf.getDatas().get(i).entrySet()) {
                    el.addElement(entry.getKey()).addText(String.valueOf(entry.getValue()));
                }
                if (CollectionUtils.isNotEmpty(leaf.getChildren())) {
                    for (SqlXmlLeaf child : leaf.getChildren()) {
                        selectXmlDataByXmlSql(stmt, child, el, leaf.getDatas().get(i));
                    }
                }
            }
        }
    }

    /**
     * 查询sql返回XML元素
     */
    protected void selectXmlDataBySql(Statement stmt, String sql, Element element) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsMataData = null;
            int count = 0;
            while (rs.next()) {
                Element el = element.addElement(HrptConstants.DataXmlAttr.DEFAULT_ROW);
                rsMataData = rs.getMetaData();
                count = rsMataData.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    el.addElement(StringUtils.upperCase(rsMataData.getColumnLabel(i))).addText(String.valueOf(rs.getObject(i)));
                }
            }
        } catch (Exception ex) {
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(null, null, rs);
        }
    }

    /**
     * 根据Xml结构SQL获取Map
     */
    protected void selectMapDataByXmlSql(Statement stmt, SqlXmlLeaf leaf, Map<String, Object> resultMap, Map<String, Object> paramMap) {
        // 执行本层级查询
        List<Map<String, Object>> thisResult = getMapDataBySql(stmt, leaf.getSql(), paramMap);
        leaf.setDatas(thisResult);
        // 查询数据注入父节点数据
        resultMap.put(leaf.getName(), thisResult);
        // 如果查询数据不为空且当前节点拥有子节点，则将当前节点数据视为子节点查询参数，执行子查询
        if (CollectionUtils.isNotEmpty(leaf.getDatas())) {
            for (int i = 0, length = leaf.getDatas().size(); i < length; i++) {
                if (CollectionUtils.isNotEmpty(leaf.getChildren())) {
                    for (SqlXmlLeaf child : leaf.getChildren()) {
                        selectMapDataByXmlSql(stmt, child, resultMap, leaf.getDatas().get(i));
                    }
                }
            }
        }
    }

    /**
     * 查询sql返回Map元素
     */
    protected void selectMapDataBySql(Statement stmt, String sql, Map<String, Object> resultMap) {
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(sql);
            ResultSetMetaData rsMataData;
            int count;
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> map;
            while (rs.next()) {
                rsMataData = rs.getMetaData();
                count = rsMataData.getColumnCount();
                map = new HashMap<>(BaseConstants.Digital.SIXTEEN);
                for (int i = 1; i <= count; i++) {
                    map.put(StringUtils.upperCase(rsMataData.getColumnLabel(i)), rs.getObject(i));
                }
                mapList.add(map);
            }
            resultMap.put(HrptConstants.DataXmlAttr.DEFAULT_DS, mapList);
        } catch (Exception ex) {
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(null, null, rs);
        }
    }

    /**
     * 获取SQL执行后XML结果
     */
    protected List<Map<String, Object>> getMapDataBySql(Statement stmt, String sql, Map<String, Object> paramMap) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        String exeSql = getExecuteSql(sql, paramMap);
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(exeSql);
            ResultSetMetaData rsMataData;
            int count;
            Map<String, Object> map;
            while (rs.next()) {
                map = new HashMap<>(BaseConstants.Digital.SIXTEEN);
                rsMataData = rs.getMetaData();
                count = rsMataData.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    map.put(StringUtils.upperCase(rsMataData.getColumnLabel(i)), rs.getObject(i) == null ? "" : rs.getObject(i));
                }
                resultMap.add(map);
            }
        } catch (Exception ex) {
            throw new CommonException(HrptMessageConstants.ERROR_REPORT_PARAMETER_SET, ex);
        } finally {
            JdbcUtils.releaseJdbcResource(null, null, rs);
        }
        return resultMap;
    }

    /**
     * SQL预处理
     */
    protected String preProcessSqlText(String sqlText) {
        // 去掉SQL后面的结束符号";"
        sqlText = StringUtils.stripEnd(sqlText, ";");
        return sqlText;
    }

    /**
     * 分页SQL预处理， 在这里可以拦截全表查询等sql， 因为如果表的数据量很大，将会产生过多的内存消耗，甚至性能问题
     *
     * @param parameter 报表相关属性参数
     * @return 预处理后的sql语句
     */
    protected String prePageSqlText(ReportParameter parameter) {
        return jdbcQuery.pageSql(parameter.getSqlText(), parameter.getSqlPageInfo()
                .setMax(ApplicationContextHelper.getContext().getBean(ReportConfig.class).getMaxRows()));
    }

    /**
     * 预处理获取查询条数的sql语句
     *
     * @param sqlText 报表sql
     * @return 预处理后的sql语句
     */
    protected String getSmartCountSql(String sqlText) {
        return jdbcQuery.countSql(sqlText);
    }

    /**
     * 获取当前报表查询器的JDBC Connection对象
     *
     * @return Connection
     */
    protected Connection getJdbcConnection() {
        return jdbcQuery.getJdbcConnection();
    }

    /**
     * 替换参数的可执行SQL
     */
    protected String getExecuteSql(String sql, Map<String, Object> paramMap) {
        Set<String> paramList = Regexs.matchString(SqlXmlParser.PARAMETER_REGEX, sql);
        for (String param : paramList) {
            String paramStr = param.substring(2, param.length() - 2);
            sql = sql.replace(param, String.valueOf(paramMap.getOrDefault(paramStr, "''")));
        }
        return sql;
    }

}
