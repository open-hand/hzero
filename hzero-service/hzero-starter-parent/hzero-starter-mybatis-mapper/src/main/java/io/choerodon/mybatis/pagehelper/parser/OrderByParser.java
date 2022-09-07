package io.choerodon.mybatis.pagehelper.parser;

import io.choerodon.mybatis.code.Style;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.util.StringUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Sort对象转sql工具类
 *
 * @author superleader8@gmail.com
 */
public class OrderByParser implements IOrderByParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderByParser.class);
    private final Style style;

    public OrderByParser(Style style) {
        this.style = style;
    }

    /**
     * Sort对象转sql
     *
     * @param sort sort
     * @param ms   MappedStatement
     * @return Sort对象转sql
     */
    @Override
    public String sortToString(Sort sort, MappedStatement ms) {
        Iterator<Sort.Order> iterator = sort.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            //前端url传入的排序列名
            String property = order.getProperty();
            String direction = order.getDirection().toString();
            if (!order.isPropertyChanged()) {
                //根据ms获取entityClass,反射获取所有字段和注解
                Class<?> entityClass = getEntityClass(ms);
                if (entityClass != null) {
                    String column = getColumn(entityClass, property);
                    splicingSql(stringBuilder, direction, column);
                } else {
                    splicingSql(stringBuilder, direction, StringUtil.convertByStyle(property, style));
                }
            } else {
                //多表联查，拼接order by暂未做校验，可能会报sql语句错误或sql注入
                splicingSql(stringBuilder, direction, property);
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    @Override
    public boolean containOrderBy(String sql) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            Select select = (Select) stmt;
            SelectBody selectBody = select.getSelectBody();
            return hasOrderBy(selectBody);
        } catch (JSQLParserException e) {
            //保守策略，如果解析失败，认为不包含order by，抛给数据库判断sql是否正确
            LOGGER.warn("JSQLParser can not parse the sql: {}, exception: {}", sql, e);
            return false;
        }
    }

    private void splicingSql(StringBuilder stringBuilder, String direction, String column) {
        stringBuilder.append(column);
        stringBuilder.append(" ");
        stringBuilder.append(direction);
        stringBuilder.append(",");
    }

    private String getColumn(Class<?> entityClass, String property) {
        //支持前端传入字段为下划线格式
        String camelHumpProperty = StringUtil.underlineToCamelhump(property);
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        // 前端传入字段与do对象字段对比，不匹配使用指定字段
        for (EntityColumn entityColumn : columnList) {
            if (entityColumn.getProperty().toLowerCase().equals(camelHumpProperty.toLowerCase())) {
                return entityColumn.getColumn();
            }
        }
        return StringUtil.convertByStyle(property, style);
    }

    private Class<?> getEntityClass(MappedStatement ms) {
        String msId = ms.getId();
        Class<?> newMapperClass = MapperTemplate.getMapperClass(msId);
        Type[] types = newMapperClass.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType t = (ParameterizedType) type;
                return (Class<?>) t.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    private boolean hasOrderBy(SelectBody selectBody) {
        if (selectBody instanceof PlainSelect) {
            PlainSelect plainSelect = (PlainSelect) selectBody;
            List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
            if (orderByElements != null && !orderByElements.isEmpty()) {
                return true;
            }
        } else if (selectBody instanceof WithItem) {
            WithItem withItem = (WithItem) selectBody;
            if (withItem.getSelectBody() != null) {
                hasOrderBy(withItem.getSelectBody());
            }
        } else {
            SetOperationList operationList = (SetOperationList) selectBody;
            if (operationList.getSelects() != null && operationList.getSelects().size() > 0) {
                List<SelectBody> plainSelects = operationList.getSelects();
                for (SelectBody plainSelect : plainSelects) {
                    hasOrderBy(plainSelect);
                }
            }
        }
        return false;
    }
}
