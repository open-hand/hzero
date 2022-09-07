package org.hzero.boot.platform.data.permission.util;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 数据屏蔽sql处理工具类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 20:27
 */
public class SqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(org.hzero.mybatis.util.SqlUtils.class);
    private static final Pattern TAG_OPEN = Pattern.compile("<[^/>]+>");
    private static final Pattern TAG_CLOSE = Pattern.compile("</[a-z]+>");
    private static final String SELF_TAG_CLOSE = "/>";
    private static final Pattern AUTOWIRED_BIND = Pattern.compile("<autowired:bind.*/>");
    private static final String AUTOWIRED_BIND_NAME = "name";
    private static final String AUTOWIRED_BIND_VALUE = "value";

    private SqlUtils() {

    }

    private static final String SELECT = "SELECT * FROM ";
    private static final String WHERE = " WHERE";
    private static final String AND = " AND";

    /**
     * 根据Table和屏蔽规则生成一个子查询
     *
     * @param table       table
     * @param sqlList     当前表的数据屏蔽规则
     * @param args        mapper参数
     * @param userDetails 用户信息
     * @return subSelect
     */
    public static SubSelect generateSubSelect(Table table, List<String> sqlList, Map args, CustomUserDetails userDetails, int tableAliasSeed) throws JSQLParserException {
        PlainSelect plainSelect = (PlainSelect) generatePlainSelect(table, sqlList, args, userDetails, tableAliasSeed).getSelectBody();
        SubSelect subSelect = new SubSelect();
        subSelect.setSelectBody(plainSelect);
        if (table.getAlias() != null) {
            subSelect.setAlias(table.getAlias());
        } else {
            Alias alias = new Alias(table.getName());
            alias.setUseAs(false);
            subSelect.setAlias(alias);
        }
        return subSelect;
    }

    /**
     * 生成一个查询select
     *
     * @param table   表名
     * @param sqlList 数据屏蔽规则
     * @return select
     * @throws JSQLParserException
     */
    private static Select generatePlainSelect(Table table, List<String> sqlList, Map args, CustomUserDetails userDetails, int tableAliasSeed) throws JSQLParserException {
        return (Select) CCJSqlParserUtil.parse(generateSubSelectSql(table, sqlList, args, userDetails, tableAliasSeed));
    }

    /**
     * 处理表名数据库前缀
     *
     * @param table  表
     * @param prefix 数据库前缀
     */
    public static Table generateTablePrefix(Table table, String prefix) {
        if (prefix != null && !"".equals(prefix.trim())) {
            //table.setDatabase(null);
            table.setSchemaName(prefix);
        }
        return table;
    }

    /**
     * 根据表名和表名维护的数据屏蔽规则生成拼接好的sql
     *
     * @param table   表
     * @param sqlList 需要拦截注入的sql
     * @return sql
     */
    private static String generateSubSelectSql(Table table, List<String> sqlList, Map args, CustomUserDetails userDetails, int tableAliasSeed) {
        StringBuilder newSql = new StringBuilder(SELECT + (
                StringUtils.isNotEmpty(table.getSchemaName()) ? table.getSchemaName() + "." : ""
        ) + table.getName());
        String tableAlias = "DST__" + tableAliasSeed;
        newSql.append(" ").append(tableAlias).append(" ").append(WHERE);
        for (String sql : sqlList) {
            newSql.append(" ").append(sql).append(AND);
        }
        return handleSql(newSql.toString().substring(0, newSql.length() - AND.length()), args, userDetails, tableAlias);
    }

    /**
     * 解析替代sql中存在的#{}表达式所指代的动态值，如果表达式存在值，则替换，否则不替换
     *
     * @param sql sql
     * @return sql
     */
    private static String handleSql(String sql, Map args, CustomUserDetails userDetails, String tableAlias) {
        if (isScript(sql)) {
            return handleScriptSql(sql, args, userDetails, tableAlias);
        }
        List<String> fieldList = StringUtils.getFieldList(sql);
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (String field : new HashSet<>(fieldList)) {
                // 获得UserInfo中field值
                Object value = DynamicValueUtils.getValue(StringUtils.getField(field), args, userDetails, tableAlias);
                if (value == null) {
                    value = field;
                }
                if (!"#{tableAlias}".equals(field)) {
                    sql = org.springframework.util.StringUtils.replace(sql, field, StringUtils.generateValueString(value));
                } else {
                    sql = org.springframework.util.StringUtils.replace(sql, field, String.valueOf(value));
                }
            }
        }
        return sql;
    }


    private static boolean isScript(String sql) {
        return StringUtils.isNotEmpty(sql)                                               // SQL 不为空
                && TAG_OPEN.matcher(sql).find()                                          // 并且包含开始标签
                && (sql.contains(SELF_TAG_CLOSE) || TAG_CLOSE.matcher(sql).find());      // 并且包含自结束标签或者结束标签
    }

    private static String handleScriptSql(String sql, Map args, CustomUserDetails userDetails, String tableAlias) {
        Properties properties = buildSqlPermissionProperties(args, userDetails, tableAlias);
        XPathParser parser = new XPathParser("<script>" + simpleOgnlTranslate(sql, properties) + "</script>", false, properties, new XMLMapperEntityResolver());
        Configuration configuration = new Configuration();
        configuration.setVariables(properties);
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration, parser.evalNode("/script"), Properties.class);
        return runnableSql(configuration, xmlScriptBuilder.parseScriptNode().getBoundSql(properties));
    }

    public static String runnableSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    private static String simpleOgnlTranslate(String sql, Properties properties) {
        // simple bind
        if (StringUtils.isNotEmpty(sql)) {
            Matcher matcher = AUTOWIRED_BIND.matcher(sql);
            while (matcher.find()) {
                String simpleBindScript = matcher.group();
                logger.debug("Find simple bind : {}", simpleBindScript);
                AutowiredBind autowiredBind = new AutowiredBind(simpleBindScript);
                if (autowiredBind.success) {
                    properties.put(autowiredBind.name, autowiredBind.bindValue);
                }
            }
            sql = sql.replaceAll(AUTOWIRED_BIND.pattern(), "");
        }
        return sql;
    }

    private static class AutowiredBind {
        private String name;
        private Object bindValue;
        private boolean success;

        public AutowiredBind(String autowiredBindScript) {
            ApplicationContext context = ApplicationContextHelper.getContext();
            if (context != null && StringUtils.isNotEmpty(autowiredBindScript)) {
                String autowiredBindScriptTrim = autowiredBindScript
                        .replace(" ", "")
                        .replace("\r", "")
                        .replace("\n", "")
                        .replace("\t", "")
                        .replace("\r\n", "");
                this.name = findProperty(AUTOWIRED_BIND_NAME, autowiredBindScriptTrim);
                String value = findProperty(AUTOWIRED_BIND_VALUE, autowiredBindScriptTrim);
                if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
                    if (value.endsWith(".class")) {
                        try {
                            this.bindValue = context.getBean(Optional.ofNullable(context.getClassLoader())
                                    .orElseGet(ClassLoader::getSystemClassLoader)
                                    .loadClass(value.substring(0, value.length() - ".class".length())));
                            this.success = true;
                        } catch (ClassNotFoundException e) {
                            logger.error("Not found class {}", value);
                        } catch (Exception e) {
                            logger.error("Not found autowired bean {}", value);
                        }
                    } else {
                        if (context.containsBean(value)) {
                            this.bindValue = context.getBean(value);
                            this.success = true;
                        } else {
                            logger.error("Not found autowired bean {}", value);
                        }
                    }
                } else {
                    logger.error("Error read properties from : {}", autowiredBindScript);
                }
            } else {
                logger.error("Error autowired bind because application context is not Initialization or script is empty.");
            }
        }

        private String findProperty(String propertyName, String autowiredBindScriptTrim) {
            char[] autowiredBindArrays = autowiredBindScriptTrim.toCharArray();
            int propertyIndex = autowiredBindScriptTrim.indexOf(propertyName);
            if (propertyIndex >= 0) {
                // 2 -> ="
                StringBuilder nameValueBuilder = new StringBuilder();
                for (int i = propertyIndex + propertyName.length() + 2; i < autowiredBindArrays.length && autowiredBindArrays[i] != '"'; i++) {
                    nameValueBuilder.append(autowiredBindArrays[i]);
                }
                return nameValueBuilder.toString();
            }
            return null;
        }
    }

    private static Properties buildSqlPermissionProperties(Map args, CustomUserDetails userDetails, String tableAlias) {
        Properties properties = new Properties();
        if (userDetails != null) {
            // UserDetails
            for (Field field : CustomUserDetails.class.getDeclaredFields()) {
                try {
                    Object value = FieldUtils.readField(field, userDetails, true);
                    if (value != null) {
                        properties.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Error read field in user details : " + field.getName(), e);
                }
            }
            // 角色合并
            properties.put("roleMergeIds", Optional.ofNullable(userDetails.roleMergeIds()).orElse(Collections.emptyList()));
        }
        // 原始参数
        // noinspection unchecked
        args.forEach((k, v) -> {
            if (k != null && v != null) {
                properties.put(k, v);
            }
        });
        // 表别名
        properties.put("tableAlias", tableAlias);
        return properties;
    }

    /**
     * 获得一个查询中所有查询的表
     *
     * @param select
     * @return tableList
     */
    public static List<Table> getAllSelectTable(PlainSelect select) {
        if (select != null) {
            List<Table> tableList = new ArrayList<>();
            FromItem fromItem = select.getFromItem();
            if (fromItem instanceof Table) {
                tableList.add((Table) fromItem);
            }
            List<Join> joinList = select.getJoins();
            if (CollectionUtils.isNotEmpty(joinList)) {
                joinList.forEach(join -> {
                    FromItem joinItem = join.getRightItem();
                    if (joinItem instanceof Table) {
                        tableList.add((Table) joinItem);
                    }
                    // 处理子查询
                    getAllTableFromSubSelect(tableList, joinItem);
                });
            }
            // 处理子查询
            getAllTableFromSubSelect(tableList, fromItem);
            return tableList;
        }
        return Collections.emptyList();
    }

    /**
     * 递归处理子查询，得到所有的表
     *
     * @param tableList
     * @param fromItem
     */
    private static void getAllTableFromSubSelect(List<Table> tableList, FromItem fromItem) {
        // 对子查询进行递归，得到子查询中的所有表
        if (hasPlainSelect(fromItem)) {
            tableList.addAll(getAllSelectTable((PlainSelect) ((SubSelect) fromItem).getSelectBody()));
        }
        // 递归处理子查询中可能的union all
        if (hasSetOperationList(fromItem)) {
            SetOperationList setOperationList = (SetOperationList) ((SubSelect) fromItem).getSelectBody();
            List<SelectBody> selectBodyList = setOperationList.getSelects();
            selectBodyList.forEach(selectBody -> {
                if (selectBody instanceof PlainSelect) {
                    tableList.addAll(getAllSelectTable((PlainSelect) selectBody));
                }
            });
        }
    }

    /**
     * 判断是否含有SetOperationList
     *
     * @param item
     * @return
     */
    private static boolean hasSetOperationList(FromItem item) {
        return item instanceof SubSelect && ((SubSelect) item).getSelectBody() instanceof SetOperationList;
    }

    /**
     * 判断是否含有PlainSelect
     *
     * @param item
     * @return
     */
    private static boolean hasPlainSelect(FromItem item) {
        return item instanceof SubSelect && ((SubSelect) item).getSelectBody() instanceof PlainSelect;
    }

    /**
     * 根据sql获取sql中被查询的表名（表名仅限于FROM之后WHERE之前的表名,且不包括子查询的表名）
     *
     * @param sql sql
     * @return tableList
     * @throws JSQLParserException
     */
    public static List<Table> getAllSelectTable(String sql) throws JSQLParserException {
        List<PlainSelect> selectList = getSelectList(sql);
        return getAllSelectTable(selectList);
    }

    /**
     * 根据sql获取sql中被查询的表名（表名仅限于FROM之后WHERE之前的表名,且不包括子查询里的表名）
     *
     * @param statement sql
     * @return tableList
     * @throws JSQLParserException
     */
    public static List<Table> getAllSelectTable(Statement statement) {
        List<PlainSelect> selectList = getSelectList(statement);
        return getAllSelectTable(selectList);
    }

    /**
     * 获得sql中的所有查询对象
     *
     * @param sql sql
     * @return selectList
     * @throws JSQLParserException
     */
    private static List<PlainSelect> getSelectList(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        return getSelectList(statement);
    }

    /**
     * 获得sql中的所有查询对象
     *
     * @param statement statement
     * @return selectList
     * @throws JSQLParserException
     */
    private static List<PlainSelect> getSelectList(Statement statement) {
        if (statement instanceof Select) {
            List<PlainSelect> selectList = new ArrayList<>();
            Select select = (Select) statement;
            Object obj = select.getSelectBody();
            if (obj instanceof PlainSelect) {
                selectList.add((PlainSelect) obj);
            } else if (obj instanceof SetOperationList) {
                SetOperationList setOperationList = (SetOperationList) obj;
                List<SelectBody> selectBodyList = setOperationList.getSelects();
                if (CollectionUtils.isNotEmpty(selectBodyList)) {
                    selectBodyList.forEach(selectBody -> {
                        if (selectBody instanceof PlainSelect) {
                            selectList.add((PlainSelect) selectBody);
                        }
                    });
                }
            }
            return selectList;
        }
        return Collections.emptyList();
    }

    /**
     * 获取PlainSelect对象的表名
     *
     * @param selectList
     * @return tableList
     */
    private static List<Table> getAllSelectTable(List<PlainSelect> selectList) {
        if (CollectionUtils.isNotEmpty(selectList)) {
            List<Table> tableList = new ArrayList<>();
            selectList.forEach(select -> tableList.addAll(getAllSelectTable(select)));
            return tableList;
        } else {
            return Collections.emptyList();
        }
    }

}
