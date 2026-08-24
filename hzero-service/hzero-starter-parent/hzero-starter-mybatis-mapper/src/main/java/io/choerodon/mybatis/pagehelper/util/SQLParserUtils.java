package io.choerodon.mybatis.pagehelper.util;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SQL解析器工具类
 *
 * @author XCXCXCXCX
 * @date 2020/2/26 10:54 上午
 */
public class SQLParserUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLParserUtils.class);

    public static Set<String> getColumns(MappedStatement statement, Object parameter) {
        try {
            List<Column> columnList = ((Update) CCJSqlParserUtil.parse(statement.getBoundSql(parameter).getSql())).getColumns();
            if (!CollectionUtils.isEmpty(columnList)) {
                return columnList.stream().map(column -> column.getColumnName().toLowerCase()).collect(Collectors.toSet());
            }
        } catch (JSQLParserException e) {
            LOGGER.error("Error parse sql, {}", ExceptionUtils.getStackTrace(e));
        }
        return Collections.emptySet();
    }

}
