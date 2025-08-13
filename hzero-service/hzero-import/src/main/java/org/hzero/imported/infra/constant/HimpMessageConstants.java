package org.hzero.imported.infra.constant;

/**
 * 
 * 错误代码常量类
 * 
 * @author zhiying.dong@hand-china.com
 */
public class HimpMessageConstants {
    /**
     * 没有选择数据源
     */
    public static final String ERROR_NOSELECT_DATASOURCE = "himp.error.noselect.datasource";
    /**
     * 没有选择数据源
     */
    public static final String ERROR_UNSUPPORTED_DATASOURCE = "himp.error.unsupported.datasource";
    /**
     * 数据源连接池类加载错误
     */
    public static final String ERROR_POOL_FACTORY_LOAD_CLASS = "himp.error.poolFactory.LoadClass";
    /**
     * {0}: 数据源连接池创建错误
     */
    public static final String ERROR_DATASOURCE_POOL_CREATE = "himp.error.datasource.pool.create";
    /**
     * 模板编码不能为空
     */
    public static final String NOTNULL_TEMPLATE_CODE = "himp.error.notnull.templateCode";
    /**
     * 模板名称不能为空
     */
    public static final String NOTNULL_TEMPLATE_NAME = "himp.error.notnull.templateName";
    /**
     * 模板类型不能为空
     */
    public static final String NOTNULL_TEMPLATE_TYPE = "himp.error.notnull.templateType";
    /**
     * 模板编码重复
     */
    public static final String DUPLICATE_TEMPLATE_CODE = "himp.error.duplicate.templateCode";
    /**
     * 头表未找到
     */
    public static final String TEMPLATE_HEADER_NOT_FOUND = "himp.error.templateHeader.notFound";
    /**
     * 列编码重复
     */
    public static final String COLUMN_INDEX_REPEAT = "himp.error.columnIndex.repeat";
    /**
     * SQL语句解析错误
     */
    public static final String ERROR_SQL_PARSER = "himp.error.sql.parser";
    /**
     * 数据源获取失败
     */
    public static final String ERROR_DATASOURCE_NOT_FOUND = "himp.error.datasource.notFound";
    /**
     * 数据库资源释放异常
     */
    public static final String ERROR_RELEASE_JDBC_RESOURCE = "himp.error.release.jdbc.resource";
    /**
     * 页序号重复
     */
    public static final String ERROR_DUPLICATE_SHEET_INDEX = "himp.error.duplicate.sheet.index";
    /**
     * 页名称重复
     */
    public static final String ERROR_DUPLICATE_SHEET_NAME = "himp.error.duplicate.sheet.name";
}
