package org.hzero.imported.domain.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.AbstractServerImportService;
import org.hzero.boot.imported.config.ImportConfig;
import org.hzero.boot.imported.domain.entity.TemplateColumn;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.validator.annotation.ImportService;
import org.hzero.boot.platform.ds.DatasourceHelper;
import org.hzero.boot.platform.ds.constant.DsConstants;
import org.hzero.boot.platform.ds.vo.DatasourceVO;
import org.hzero.imported.infra.constant.HimpConstants;
import org.hzero.imported.infra.constant.HimpMessageConstants;
import org.hzero.jdbc.Update;
import org.hzero.jdbc.constant.DBPoolTypeEnum;
import org.hzero.jdbc.constant.DatabaseTypeEnum;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;

/**
 * 服务端通用导入服务类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月18日上午10:51:15
 */
@ImportService(templateCode = HimpBootConstants.SERVICE_IMPORT)
public class ImportServiceImpl extends AbstractServerImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportServiceImpl.class);

    private List<Connection> connectionList = new ArrayList<>();

    private DatasourceHelper datasourceHelper;
    private ObjectMapper objectMapper;
    private ImportConfig importConfig;

    @Autowired
    public ImportServiceImpl(DatasourceHelper datasourceHelper,
                             ObjectMapper objectMapper,
                             ImportConfig importConfig) {
        this.datasourceHelper = datasourceHelper;
        this.objectMapper = objectMapper;
        this.importConfig = importConfig;
    }

    @Override
    public int getSize() {
        return importConfig.getBatchSize();
    }

    @Override
    public Boolean doImport(List<String> dataList) {
        // 获取数据源
        DatasourceStatement importDataSource = this.getImportDataSource(currentTemplatePage.getTenantId(), currentTemplatePage.getDatasourceCode());
        // 解析SQL
        List<String> sqlTexts;
        try {
            sqlTexts = generateSql(dataList, importDataSource.getDbType());
        } catch (Exception e) {
            throw new CommonException(HimpMessageConstants.ERROR_SQL_PARSER, e);
        }
        // 执行SQL
        if (!CollectionUtils.isEmpty(sqlTexts)) {
            this.executeSqlText(importDataSource, sqlTexts);
        }
        if (dataList.size() < importConfig.getBatchSize()) {
            // 提交事务
            connectionList.forEach(connection -> {
                try {
                    connection.commit();
                    connection.close();
                } catch (SQLException e) {
                    logger.warn("commit failed!");
                    throw new CommonException(e);
                }
            });
            connectionList.clear();
        }
        return true;
    }

    private List<String> generateSql(List<String> dataList, String databaseType) throws IOException {
        List<String> sql;
        if (QueryConstants.Datasource.DB_MSSQL.equals(databaseType)) {
            sql = Collections.singletonList(generateInsertSqlByMSSQL(dataList));
        } else if (QueryConstants.Datasource.DB_ORACLE.equals(databaseType)) {
            sql = Collections.singletonList(generateInsertSqlByOracle(dataList));
        } else if (QueryConstants.Datasource.DB_TIDB.equals(databaseType) || QueryConstants.Datasource.DB_MYSQL.equals(databaseType)) {
            sql = Collections.singletonList(generateInsertSqlByMySql(dataList));
        } else {
            sql = generateMultiInsertSql(dataList);
        }
        return sql.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    private List<String> generateMultiInsertSql(List<String> dataList) throws IOException {
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<TemplateColumn> templateColumnList = currentTemplatePage.getTemplateColumnList()
                .stream()
                .sorted(Comparator.comparingInt(TemplateColumn::getColumnIndex))
                .collect(Collectors.toList());
        List<String> batchSql = new ArrayList<>();
        for (String data : dataList) {
            Map<String, Object> dataMap = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
            batchSql.add("INSERT INTO " +
                    currentTemplatePage.getTableName() +
                    "(" +
                    StringUtils.join(templateColumnList.stream()
                            .map(TemplateColumn::getColumnCode)
                            .collect(Collectors.toList()), ", ") +
                    ") VALUES (" +
                    StringUtils.join(templateColumnList.stream()
                            .map(line -> getColumnValue(null, line.getColumnType(), line.getFormatMask(), dataMap.get(line.getColumnCode())))
                            .collect(Collectors.toList()), ", ") +
                    ")");
        }
        return batchSql;
    }

    /**
     * MySQL插入语句
     *
     * @param dataList 数据
     * @return 插入语句
     * @throws IOException 异常
     */
    private String generateInsertSqlByMySql(List<String> dataList) throws IOException {
        List<TemplateColumn> templateColumnList = currentTemplatePage.getTemplateColumnList();
        final String columns = getImportColumns(templateColumnList);
        final StringBuilder values = new StringBuilder();
        // 组装插入值
        for (String data : dataList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.readValue(data, Map.class);
            values.append("(");
            templateColumnList
                    .forEach(line -> values
                            .append(getColumnValue(DatabaseTypeEnum.MYSQL, line.getColumnType(),
                                    line.getFormatMask(), dataMap.get(line.getColumnCode())))
                            .append(","));
            values.replace(values.length() - 1, values.length(), "),");
        }
        if (values.length() > 1) {
            values.deleteCharAt(values.length() - 1);
            return "INSERT INTO " + currentTemplatePage.getTableName() + "(" + columns + ") VALUES " + values;
        } else {
            return null;
        }
    }

    /**
     * @param dataList 数据集合
     * @return SQL
     */
    private String generateInsertSqlByMSSQL(List<String> dataList) throws IOException {
        List<TemplateColumn> templateColumnList = currentTemplatePage.getTemplateColumnList();
        final String columns = getImportColumns(templateColumnList);
        final StringBuilder values = new StringBuilder();
        // 组装插入值
        for (String data : dataList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.readValue(data, Map.class);
            values.append("(");
            templateColumnList
                    .forEach(line -> values
                            .append(getColumnValue(DatabaseTypeEnum.SQLSERVER, line.getColumnType(),
                                    line.getFormatMask(), dataMap.get(line.getColumnCode())))
                            .append(","));
            values.replace(values.length() - 1, values.length(), "),");
        }
        if (values.length() > 1) {
            values.deleteCharAt(values.length() - 1);
            return "INSERT INTO " + currentTemplatePage.getTableName() + "(" + columns + ") VALUES " + values;
        } else {
            return null;
        }
    }

    /**
     * TODO Oracle插入语句
     *
     * @param dataList 数据集合
     * @return SQL
     */
    private String generateInsertSqlByOracle(List<String> dataList) throws IOException {
        List<TemplateColumn> templateColumnList = currentTemplatePage.getTemplateColumnList();
        final String columns = getImportColumns(templateColumnList);
        final StringBuilder values = new StringBuilder();
        final StringBuilder lines = new StringBuilder();
        // 组装插入值
        int i = 1;
        int size = dataList.size();
        for (String data : dataList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = objectMapper.readValue(data, Map.class);
            // 处理SQL外层查询字段
            if (i == 1) {
                values.append("SELECT ");
                for (TemplateColumn column : templateColumnList) {
                    // 如果Long类型且值为序列，则序列作为查询列字段
                    if (HimpConstants.ColumnType.SEQUENCE.equals(column.getColumnType())) {
                        values.append(dataMap.get(column.getColumnCode())).append(",");
                    } else {
                        values.append("t.").append(column.getColumnCode()).append(",");
                    }
                }
                values.deleteCharAt(values.length() - 1);
                // 拼接子查询
                values.append(" FROM ( ");
            }
            lines.append("SELECT ");
            for (TemplateColumn line : templateColumnList) {
                // 如果Long类型且值为序列，则序列作为查询列字段
                if (!(HimpConstants.ColumnType.SEQUENCE.equals(line.getColumnType()))) {
                    lines.append(getColumnValue(DatabaseTypeEnum.ORACLE, line.getColumnType(), line.getFormatMask(),
                            dataMap.get(line.getColumnCode()))).append(" ").append(line.getColumnCode())
                            .append(" ,");
                }
            }
            lines.deleteCharAt(lines.length() - 1);
            lines.append(" FROM dual ");
            if (i != size) {
                lines.append(" UNION ALL ");
            }
            values.append(lines);
            lines.delete(0, lines.length());
            i++;
        }
        values.append(" ) t");
        return "INSERT INTO " + currentTemplatePage.getTableName() + "(" + columns + ") " + values;
    }

    /**
     * 获取导入列
     *
     * @return 导入列字符串
     */
    private String getImportColumns(List<TemplateColumn> templateColumnList) {
        final StringBuilder columns = new StringBuilder();
        // 组装插入字段
        templateColumnList.forEach(line -> columns.append(line.getColumnCode()).append(","));
        columns.deleteCharAt(columns.length() - 1);
        return columns.toString();
    }

    /**
     * 获取插入字段值
     *
     * @param type  类型
     * @param value 值
     * @return MySQL插入字段值
     */
    private String getColumnValue(DatabaseTypeEnum dbtypeEnum, String type, String formatMask, Object value) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case HimpConstants.ColumnType.DATE:
                if (StringUtils.isNotBlank(formatMask) && dbtypeEnum == DatabaseTypeEnum.ORACLE) {
                    String fm = formatMask;
                    int flag = StringUtils.indexOfIgnoreCase(formatMask, "hh");
                    if (flag > 0) {
                        fm = formatMask.split(" ")[0] + " HH24:mi:ss";
                    }
                    return "to_date('" + value + "','" + fm + "')";
                } else {
                    return "'" + value + "'";
                }
            case HimpConstants.ColumnType.DECIMAL:
            case HimpConstants.ColumnType.LONG:
                return String.valueOf(value);
            case HimpConstants.ColumnType.STRING:
                return "'" + value.toString().replaceAll("'", "\\\\'") + "'";
            default:
                return null;
        }
    }

    /**
     * 获取导入数据源
     *
     * @param tenantId       租户Id
     * @param datasourceCode 数据源编码
     * @return 导入数据源
     */
    private DatasourceStatement getImportDataSource(Long tenantId, String datasourceCode) {
        DatasourceVO dataSource = datasourceHelper.getDatasource(DsConstants.DsPurpose.DI, tenantId, datasourceCode);
        Assert.notNull(dataSource, HimpMessageConstants.ERROR_DATASOURCE_NOT_FOUND);
        return getImportDataSource(tenantId, datasourceCode, dataSource);
    }

    /**
     * 获取导入数据源
     *
     * @param ds 数据源
     * @return 导入数据源
     */
    private DatasourceStatement getImportDataSource(Long tenantId, String datasourceCode, DatasourceVO ds) {
        Map<String, Object> options = new HashMap<>(3);
        if (StringUtils.isNotEmpty(ds.getOptions())) {
            try {
                options = objectMapper.readValue(ds.getOptions(), new TypeReference<Map<String, Object>>() {
                });
            } catch (IOException e) {
                throw new CommonException(e);
            }
        }
        return new DatasourceStatement(tenantId, datasourceCode, ds.getDriverClass(), ds.getDatasourceUrl(), ds.getUsername(),
                ds.getPasswordEncrypted(), DatabaseTypeEnum.valueOf2(ds.getDbType()), DBPoolTypeEnum.valueOf2(ds.getDbPoolType()), options);
    }


    /**
     * 执行SQL
     *
     * @param importDataSource 导入数据源
     * @param sqlTexts         Sql
     */
    private void executeSqlText(DatasourceStatement importDataSource, List<String> sqlTexts) {
        try {
            Update sqlExecutor = new Update(importDataSource);
            if (sqlTexts.size() == 1) {
                connectionList.add(sqlExecutor.execute(sqlTexts.get(0)));
            } else {
                connectionList.add(sqlExecutor.execute(sqlTexts));
            }

        } catch (Exception e) {
            // 回滚
            for (Connection connection : connectionList) {
                try {
                    connection.rollback();
                    connection.close();
                } catch (SQLException ex) {
                    logger.error("rollback failed!", ex);
                }
            }
            connectionList.clear();
            throw e;
        }
    }
}
