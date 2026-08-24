package org.hzero.report.infra.engine.query;

import org.dom4j.Document;
import org.hzero.report.infra.engine.data.MetaDataColumn;
import org.hzero.report.infra.engine.data.MetaDataRow;
import org.hzero.report.infra.engine.data.ReportQueryParamItem;

import java.util.List;
import java.util.Map;

/**
 * 报表查询接口
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午4:16:44
 */
public interface Query {

    /**
     * 获取报表原始数据列集合
     *
     * @return List[MetaDataColumn]
     */
    List<MetaDataColumn> getMetaDataColumns();

    /**
     * 从sql语句中解析出报表元数据列集合
     *
     * @param sqlText sql语句
     * @return List[MetaDataColumn]
     */
    List<MetaDataColumn> parseMetaDataColumns(String sqlText);

    /**
     * 从sql语句中解析出报表查询参数(如下拉列表参数）的列表项集合
     *
     * @param sqlText sql语句
     * @return List[QueryParamItem]
     */
    List<ReportQueryParamItem> parseQueryParamItems(String sqlText);

    /**
     * 获取报表原始数据行集合
     *
     * @return List[MetaDataRow]
     */
    List<MetaDataRow> getMetaDataRows();

    /**
     * 获取元数据行XML数据
     *
     * @param sqlType sql类型
     * @param sqlText  sql
     * @return doc
     */
    Document getMetaDataXml(String sqlType, String sqlText);

    /**
     * 获取元数据行Map数据
     *
     * @param sqlType sql类型
     * @param sqlText  sql
     * @return map
     */
    Map<String, Object> getMetaDataMap(String sqlType, String sqlText);

    /**
     * 获取元数据总条数
     *
     * @return 行总条数
     */
    long getMetaDataCount();

    /**
     * 获取元数据总条数
     *
     * @param sqlText sql语句
     * @return 行总条数
     */
    long getMetaDataCount(String sqlText);

}
