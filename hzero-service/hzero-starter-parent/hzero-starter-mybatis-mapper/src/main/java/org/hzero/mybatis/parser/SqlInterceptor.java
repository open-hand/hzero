package org.hzero.mybatis.parser;

import io.choerodon.core.oauth.CustomUserDetails;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 在Mybatis 拦截器中改写SQL，实现该接口时按需重写自己需要改写SQL的部分即可
 *
 * @author qingsheng.chen@hand-china.com 2019-04-28 10:47
 */
public interface SqlInterceptor {
    /**
     * 在拦截之前执行
     */
    default void before() {
    }

    /**
     * 在拦截之后执行
     */
    default void after() {
    }

    /**
     * @return 是否拦截 SELECT 语句
     */
    default boolean select() {
        return false;
    }

    /**
     * @return 是否拦截 INSERT 语句
     */
    default boolean insert() {
        return false;
    }

    /**
     * @return 是否拦截 UPDATE 语句
     */
    default boolean update() {
        return false;
    }

    /**
     * @return 是否拦截 DELETE 语句
     */
    default boolean delete() {
        return false;
    }

    /**
     * @return 拦截器抛出异常时是否中断SQL调用
     */
    default boolean interrupted() {
        return false;
    }

    /**
     * @return SQL 解析拦截器顺序
     */
    default int order() {
        return 0;
    }

    /**
     * 改写SQL
     *
     * @param statement   statement
     * @param serviceName 服务名称
     * @param sqlId       MyBatis SQL ID
     * @param args        Sql执行参数
     * @param userDetails 用户信息
     * @return 改写后的对象
     */
    default Statement handleStatement(Statement statement, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        // 查询
        if (select() && statement instanceof Select) {
            statement = handleSelect((Select) statement, serviceName, sqlId, args, userDetails);
        } else if (insert() && statement instanceof Insert) {
            statement = handleInsert((Insert) statement, serviceName, sqlId, args, userDetails);
        } else if (update() && statement instanceof Update) {
            statement = handleUpdate((Update) statement, serviceName, sqlId, args, userDetails);
        } else if (delete() && statement instanceof Delete) {
            statement = handleDelete((Delete) statement, serviceName, sqlId, args, userDetails);
        }
        return statement;
    }

    default Select handleSelect(Select select, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        select.setSelectBody(handleSelectBody(select.getSelectBody(), serviceName, sqlId, args, userDetails));
        return select;
    }

    default SelectBody handleSelectBody(SelectBody selectBody, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        // 普通SQL
        if (selectBody instanceof PlainSelect) {
            selectBody = handlePlainSelect((PlainSelect) selectBody, serviceName, sqlId, args, userDetails);
        } else /* 复杂Sql(UNION,INTERSECT,MINUS,EXCEPT) */ if (selectBody instanceof SetOperationList) {
            selectBody = handleSetOperationList((SetOperationList) selectBody, serviceName, sqlId, args, userDetails);
        }
        return selectBody;
    }

    default SetOperationList handleSetOperationList(SetOperationList setOperationList, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        List<SelectBody> selects = setOperationList.getSelects();
        if (!CollectionUtils.isEmpty(selects)) {
            for (int i = 0; i < selects.size(); ++i) {
                selects.set(i, handleSelectBody(selects.get(i), serviceName, sqlId, args, userDetails));
            }
        }
        return setOperationList;
    }

    default PlainSelect handlePlainSelect(PlainSelect plainSelect, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        // 拦截列
        List<SelectItem> selectItems = plainSelect.getSelectItems();
        if (!CollectionUtils.isEmpty(selectItems)) {
            for (int i = 0; i < selectItems.size(); ++i) {
                SelectItem selectItem = selectItems.get(i);
                if (selectItem instanceof SelectExpressionItem && ((SelectExpressionItem) selectItem).getExpression() instanceof SubSelect) {
                    SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                    selectExpressionItem.setExpression(handleSubSelect((SubSelect) selectExpressionItem.getExpression(), serviceName, sqlId, args, userDetails));
                } else if (selectItem instanceof SelectExpressionItem && ((SelectExpressionItem) selectItem).getExpression() instanceof CaseExpression) {
                    SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
                    selectExpressionItem.setExpression(handleCase((CaseExpression) selectExpressionItem.getExpression(), serviceName, sqlId, args, userDetails));
                } else {
                    selectItem = handleSelectItem(selectItem, serviceName, sqlId, args, userDetails);
                }
                selectItems.set(i, selectItem);
            }
        }
        // 拦截FROM
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            FromItem afterHandlerFromItem = handleTable((Table) fromItem, serviceName, sqlId, args, userDetails);
            plainSelect.setFromItem(afterHandlerFromItem);
        } else if (fromItem instanceof SubSelect) {
            handleSubSelect((SubSelect) fromItem, serviceName, sqlId, args, userDetails);
        }
        // 拦截JOIN
        List<Join> joins = plainSelect.getJoins();
        if (!CollectionUtils.isEmpty(joins)) {
            for (Join join : joins) {
                handleJoin(join, serviceName, sqlId, args, userDetails);
            }
        }
        // 拦截WHERE
        Expression where = plainSelect.getWhere();
        if (where != null) {
            handleExpression(where, serviceName, sqlId, args, userDetails);
        }
        return plainSelect;
    }

    default SelectItem handleSelectItem(SelectItem selectItem, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        return selectItem;
    }

    default SubSelect handleSubSelect(SubSelect subSelect, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        subSelect.setSelectBody(handleSelectBody(subSelect.getSelectBody(), serviceName, sqlId, args, userDetails));
        return subSelect;
    }

    default Expression handleCase(CaseExpression expression, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        // CASE ...
        handleExpression(expression.getSwitchExpression(), serviceName, sqlId, args, userDetails);
        // WHEN ... THEN ...
        List<WhenClause> whenClauses = expression.getWhenClauses();
        if (!CollectionUtils.isEmpty(whenClauses)) {
            for (WhenClause whenClause : whenClauses) {
                handleExpression(whenClause.getWhenExpression(), serviceName, sqlId, args, userDetails);
                handleExpression(whenClause.getThenExpression(), serviceName, sqlId, args, userDetails);
            }
        }
        // ELSE
        handleExpression(expression.getElseExpression(), serviceName, sqlId, args, userDetails);
        return expression;
    }

    default void handleJoin(Join join, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        FromItem joinFromItem = join.getRightItem();
        if (joinFromItem instanceof Table) {
            join.setRightItem(handleTable((Table) joinFromItem, serviceName, sqlId, args, userDetails));
        } else if (joinFromItem instanceof SubSelect) {
            handleSubSelect((SubSelect) joinFromItem, serviceName, sqlId, args, userDetails);
        }
    }

    default FromItem handleTable(Table table, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        return table;
    }

    default void handleExpression(Expression expression, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        if (expression instanceof AndExpression) {
            handleExpression(((AndExpression) expression).getLeftExpression(), serviceName, sqlId, args, userDetails);
            handleExpression(((AndExpression) expression).getRightExpression(), serviceName, sqlId, args, userDetails);
        }
        if (expression instanceof OrExpression) {
            handleExpression(((OrExpression) expression).getLeftExpression(), serviceName, sqlId, args, userDetails);
            handleExpression(((OrExpression) expression).getRightExpression(), serviceName, sqlId, args, userDetails);
        }
        if (expression instanceof ExistsExpression) {
            handleExpression(((ExistsExpression) expression).getRightExpression(), serviceName, sqlId, args, userDetails);
        }
        if (expression instanceof InExpression) {
            handleExpression(((InExpression) expression).getLeftExpression(), serviceName, sqlId, args, userDetails);
            handleItemsList(((InExpression) expression).getLeftItemsList(), serviceName, sqlId, args, userDetails);
            handleItemsList(((InExpression) expression).getRightItemsList(), serviceName, sqlId, args, userDetails);
        }
        if (expression instanceof SubSelect) {
            handleSubSelect((SubSelect) expression, serviceName, sqlId, args, userDetails);
        }
        if (expression instanceof Parenthesis) {
            handleExpression(((Parenthesis) expression).getExpression(), serviceName, sqlId, args, userDetails);
        }
    }

    default void handleItemsList(ItemsList itemsList, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        if (itemsList instanceof SubSelect) {
            handleSubSelect((SubSelect) itemsList, serviceName, sqlId, args, userDetails);
        }
        if (itemsList instanceof ExpressionList && !CollectionUtils.isEmpty(((ExpressionList) itemsList).getExpressions())) {
            for (Expression expression : ((ExpressionList) itemsList).getExpressions()) {
                handleExpression(expression, serviceName, sqlId, args, userDetails);
            }
        }
        if (itemsList instanceof MultiExpressionList && !CollectionUtils.isEmpty(((MultiExpressionList) itemsList).getExprList())) {
            for (ExpressionList expressionList : ((MultiExpressionList) itemsList).getExprList()) {
                handleItemsList(expressionList, serviceName, sqlId, args, userDetails);
            }
        }
    }


    default Update handleUpdate(Update update, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        return update;
    }

    default Insert handleInsert(Insert insert, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        return insert;
    }

    default Delete handleDelete(Delete delete, String serviceName, String sqlId, Map args, CustomUserDetails userDetails) {
        return delete;
    }
}
