/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hzero.starter.sqlparser.sql.dialect.oracle.visitor;

import org.hzero.starter.sqlparser.sql.SQLUtils;
import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.SQLName;
import org.hzero.starter.sqlparser.sql.ast.SQLObject;
import org.hzero.starter.sqlparser.sql.ast.SQLStatement;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLBinaryOpExpr;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLBinaryOperator;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLCharExpr;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLIdentifierExpr;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLPropertyExpr;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLAlterTableStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLAssignItem;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLBlockStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLCheck;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLColumnDefinition;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLCreateIndexStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLCreateTableStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLDeleteStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLExprTableSource;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLForeignKeyImpl;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLInsertStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLMergeStatement.MergeInsertClause;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLMergeStatement.MergeUpdateClause;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLScriptCommitStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelect;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSetStatement;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLTableSource;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLUnique;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.CycleClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.CellAssignment;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.CellAssignmentItem;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.MainModelClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.ModelColumn;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.ModelColumnClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.ModelRulesClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.QueryPartitionClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause.ReturnRowsClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.OracleReturningClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.OracleStorageClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.OracleWithSubqueryEntry;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.SampleClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.SearchClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleAnalytic;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleArgumentExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleIsOfTypeExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleRangeExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleSizeExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr.OracleTreatExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCheck;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleContinueStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreatePackageStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreateSynonymStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleCreateTypeStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExecuteImmediateStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleForStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleForeignKey;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.ConditionalInsertClauseItem;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement.InsertIntoClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OraclePipeRowStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleRaiseStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleRunStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.CheckOption;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectRestriction.ReadOnly;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUnique;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.hzero.starter.sqlparser.sql.visitor.SchemaStatVisitor;
import org.hzero.starter.sqlparser.stat.TableStat;
import org.hzero.starter.sqlparser.stat.TableStat.Column;
import org.hzero.starter.sqlparser.stat.TableStat.Mode;
import org.hzero.starter.sqlparser.util.FnvHash;
import org.hzero.starter.sqlparser.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class OracleSchemaStatVisitor extends SchemaStatVisitor implements OracleASTVisitor {

    public OracleSchemaStatVisitor(){
        this(new ArrayList<Object>());
    }

    public OracleSchemaStatVisitor(List<Object> parameters){
        super(JdbcConstants.ORACLE, parameters);
    }

    protected Column getColumn(SQLExpr expr) {
        if (expr instanceof OracleOuterExpr) {
            expr = ((OracleOuterExpr) expr).getExpr();
        }

        return super.getColumn(expr);
    }

    public boolean visit(OracleSelectTableReference x) {
        SQLExpr expr = x.getExpr();
        TableStat stat = getTableStat(x);

        if (expr instanceof SQLName) {
            if (((SQLName) expr).nameHashCode64() == FnvHash.Constants.DUAL) {
                return false;
            }

            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                if (isSubQueryOrParamOrVariant(propertyExpr)) {
                    return false;
                }
            } else if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
                if (isSubQueryOrParamOrVariant(identifierExpr)) {
                    return false;
                }
            }

            Mode mode = getMode();
            switch (mode) {
                case Delete:
                    stat.incrementDeleteCount();
                    break;
                case Insert:
                    stat.incrementInsertCount();
                    break;
                case Update:
                    stat.incrementUpdateCount();
                    break;
                case Select:
                    stat.incrementSelectCount();
                    break;
                case Merge:
                    stat.incrementMergeCount();
                    break;
                default:
                    break;
            }

            return false;
        }

        // accept(x.getExpr());

        return false;
    }

    public void endVisit(SQLSelect x) {
    }

    public boolean visit(OracleUpdateStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        setMode(x, Mode.Update);

        SQLTableSource tableSource = x.getTableSource();
        SQLExpr tableExpr = null;

        if (tableSource instanceof SQLExprTableSource) {
            tableExpr = ((SQLExprTableSource) tableSource).getExpr();
        }

        if (tableExpr instanceof SQLName) {
            TableStat stat = getTableStat((SQLName) tableExpr);
            stat.incrementUpdateCount();
        } else {
            tableSource.accept(this);
        }

        accept(x.getItems());
        accept(x.getWhere());

        return false;
    }

    public void endVisit(OracleUpdateStatement x) {
    }

    public boolean visit(OracleDeleteStatement x) {
        return visit((SQLDeleteStatement) x);
    }

    public void endVisit(OracleDeleteStatement x) {
    }

    public boolean visit(OracleSelectQueryBlock x) {
        SQLExprTableSource into = x.getInto();
//        if (into != null && into.getExpr() instanceof SQLName) {
//            TableStat stat = getTableStat((SQLName) into.getExpr());
//            if (stat != null) {
//                stat.incrementInsertCount();
//            }
//        }

        return visit((SQLSelectQueryBlock) x);
    }

    public void endVisit(OracleSelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    public boolean visit(SQLPropertyExpr x) {
        if ("ROWNUM".equalsIgnoreCase(x.getName())) {
            return false;
        }

        return super.visit(x);
    }

    public boolean visit(SQLIdentifierExpr x) {
        String name = x.getName();

        if ("+".equalsIgnoreCase(name)) {
            return false;
        }

        long hashCode64 = x.hashCode64();

        if (hashCode64 == FnvHash.Constants.ROWNUM
                || hashCode64 == FnvHash.Constants.SYSDATE
                || hashCode64 == FnvHash.Constants.LEVEL
                || hashCode64 == FnvHash.Constants.SQLCODE) {
            return false;
        }

        if (hashCode64 == FnvHash.Constants.ISOPEN
                && x.getParent() instanceof SQLBinaryOpExpr
                && ((SQLBinaryOpExpr) x.getParent()).getOperator() == SQLBinaryOperator.Modulus) {
            return false;
        }

        return super.visit(x);
    }

    @Override
    public void endVisit(OracleAnalytic x) {

    }

    @Override
    public void endVisit(OracleAnalyticWindowing x) {

    }

    @Override
    public void endVisit(OracleDbLinkExpr x) {

    }

    @Override
    public void endVisit(OracleIntervalExpr x) {

    }

    @Override
    public void endVisit(OracleOuterExpr x) {

    }

    @Override
    public void endVisit(OracleSelectJoin x) {

    }

    @Override
    public void endVisit(OracleSelectPivot x) {

    }

    @Override
    public void endVisit(Item x) {

    }

    @Override
    public void endVisit(CheckOption x) {

    }

    @Override
    public void endVisit(ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public boolean visit(SQLScriptCommitStatement astNode) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalytic x) {

        return true;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {

        return true;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {
        super.visit(x);
//
//        for (SQLExpr item : x.getUsing()) {
//            if (item instanceof SQLIdentifierExpr) {
//                String columnName = ((SQLIdentifierExpr) item).getName();
//                String leftTable = (String) x.getLeft().getAttribute(ATTR_TABLE);
//                String rightTable = (String) x.getRight().getAttribute(ATTR_TABLE);
//                if (leftTable != null && rightTable != null) {
//                    Relationship relationship = new Relationship();
//                    relationship.setLeft(new Column(leftTable, columnName));
//                    relationship.setRight(new Column(rightTable, columnName));
//                    relationship.setOperator("USING");
//                    relationships.add(relationship);
//                }
//
//                if (leftTable != null) {
//                    addColumn(leftTable, columnName);
//                }
//
//                if (rightTable != null) {
//                    addColumn(rightTable, columnName);
//                }
//            }
//        }

        return false;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {

        return true;
    }

    @Override
    public boolean visit(Item x) {

        return true;
    }

    @Override
    public boolean visit(CheckOption x) {

        return true;
    }

    @Override
    public boolean visit(ReadOnly x) {

        return true;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {
        accept(x.getSelect());
        accept(x.getPivot());
        accept(x.getFlashback());
        return false;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {

        return true;
    }

    @Override
    public boolean visit(SampleClause x) {
        return true;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {

        return true;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {
        x.getSubQuery().accept(this);
        return false;
    }

    @Override
    public void endVisit(OracleWithSubqueryEntry x) {

    }

    @Override
    public boolean visit(SearchClause x) {

        return true;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {

        return true;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

    @Override
    public boolean visit(OracleCursorExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {

        return true;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ReturnRowsClause x) {

        return true;
    }

    @Override
    public void endVisit(ReturnRowsClause x) {

    }

    @Override
    public boolean visit(MainModelClause x) {

        return true;
    }

    @Override
    public void endVisit(MainModelClause x) {

    }

    @Override
    public boolean visit(ModelColumnClause x) {

        return true;
    }

    @Override
    public void endVisit(ModelColumnClause x) {

    }

    @Override
    public boolean visit(QueryPartitionClause x) {

        return true;
    }

    @Override
    public void endVisit(QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelColumn x) {

        return true;
    }

    @Override
    public void endVisit(ModelColumn x) {

    }

    @Override
    public boolean visit(ModelRulesClause x) {

        return true;
    }

    @Override
    public void endVisit(ModelRulesClause x) {

    }

    @Override
    public boolean visit(CellAssignmentItem x) {

        return true;
    }

    @Override
    public void endVisit(CellAssignmentItem x) {

    }

    @Override
    public boolean visit(CellAssignment x) {

        return true;
    }

    @Override
    public void endVisit(CellAssignment x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        return true;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(MergeUpdateClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeUpdateClause x) {

    }

    @Override
    public boolean visit(MergeInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(MergeInsertClause x) {

    }

    @Override
    public boolean visit(OracleReturningClause x) {
        return true;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return visit((SQLInsertStatement) x);
    }

    @Override
    public void endVisit(OracleInsertStatement x) {
        endVisit((SQLInsertStatement) x);
    }

    @Override
    public boolean visit(InsertIntoClause x) {

        if (x.getTableName() instanceof SQLName) {
            TableStat stat = getTableStat(x.getTableName());
            stat.incrementInsertCount();
        }

        accept(x.getColumns());
        accept(x.getQuery());
        accept(x.getReturning());
        accept(x.getErrorLogging());

        return false;
    }

    @Override
    public void endVisit(InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Insert);

        accept(x.getSubQuery());
        accept(x.getEntries());

        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(ConditionalInsertClause x) {
        return true;
    }

    @Override
    public void endVisit(ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(ConditionalInsertClauseItem x) {
        SQLObject parent = x.getParent();
        if (parent instanceof ConditionalInsertClause) {
            parent = parent.getParent();
        }
        if (parent instanceof OracleMultiInsertStatement) {
            SQLSelect subQuery = ((OracleMultiInsertStatement) parent).getSubQuery();
        }
        x.getWhen().accept(this);
        x.getThen().accept(this);
        return false;
    }

    @Override
    public void endVisit(ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {

    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        getTableStat(x.getTable());
        return false;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {

    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleDatetimeExpr x) {

    }

    @Override
    public boolean visit(OracleSysdateExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleSysdateExpr x) {

    }

    @Override
    public void endVisit(org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {

    }

    @Override
    public boolean visit(org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleExceptionStatement.Item x) {
        SQLExpr when = x.getWhen();
        if (when instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr ident = (SQLIdentifierExpr) when;
            if (!ident.getName().equalsIgnoreCase("OTHERS")) {
                this.visit(ident);
            }
        } else if (when != null) {
            when.accept(this);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(OracleExceptionStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {

    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleArgumentExpr x) {

    }

    @Override
    public boolean visit(OracleSetTransactionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleSetTransactionStatement x) {

    }

    @Override
    public boolean visit(OracleExplainStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExplainStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableDropPartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableDropPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableTruncatePartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableTruncatePartition x) {

    }

    @Override
    public void endVisit(SQLAlterTableStatement x) {
    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.TableSpaceItem x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.TableSpaceItem x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.UpdateIndexesClause x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition.NestedTablePartitionSpec x) {

    }

    @Override
    public boolean visit(OracleAlterTableSplitPartition x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableSplitPartition x) {

    }

    @Override
    public boolean visit(OracleAlterTableModify x) {
        SQLAlterTableStatement stmt = (SQLAlterTableStatement) x.getParent();
        SQLName tableName = stmt.getName();
        String table = tableName.toString();

        for (SQLColumnDefinition column : x.getColumns()) {
            SQLName columnName = column.getName();
            addColumn(table, columnName.toString());

        }

        return false;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return visit((SQLCreateIndexStatement) x);
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {
    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        x.getRange().accept(this);
        accept(x.getStatements());
        return false;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement.Rebuild x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement.Rebuild x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        accept(x.getColumns());

        return false;
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        this.visit((SQLCreateTableStatement) x);

        if (x.getSelect() != null) {
            x.getSelect().accept(this);
        }

        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {
        this.endVisit((SQLCreateTableStatement) x);
    }

    @Override
    public boolean visit(OracleStorageClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleStorageClause x) {

    }

    @Override
    public boolean visit(OracleGotoStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleGotoStatement x) {

    }

    @Override
    public boolean visit(OracleLabelStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleLabelStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTriggerStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTriggerStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSynonymStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterSynonymStatement x) {

    }

    @Override
    public boolean visit(OracleAlterViewStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterViewStatement x) {

    }

    @Override
    public boolean visit(OracleAlterTableMoveTablespace x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableMoveTablespace x) {

    }

    @Override
    public boolean visit(OracleSizeExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleSizeExpr x) {

    }

    @Override
    public boolean visit(OracleFileSpecification x) {
        return false;
    }

    @Override
    public void endVisit(OracleFileSpecification x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceAddDataFile x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceAddDataFile x) {

    }

    @Override
    public boolean visit(OracleAlterTablespaceStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterTablespaceStatement x) {

    }

    @Override
    public boolean visit(OracleExitStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExitStatement x) {

    }

    @Override
    public boolean visit(OracleContinueStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleContinueStatement x) {

    }

    @Override
    public boolean visit(OracleRaiseStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleRaiseStatement x) {

    }

    @Override
    public boolean visit(OracleCreateDatabaseDbLinkStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateDatabaseDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDropDbLinkStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleDropDbLinkStatement x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalYear x) {
        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalYear x) {

    }

    @Override
    public boolean visit(OracleDataTypeIntervalDay x) {
        return false;
    }

    @Override
    public void endVisit(OracleDataTypeIntervalDay x) {

    }

    @Override
    public boolean visit(OracleUsingIndexClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleUsingIndexClause x) {

    }

    @Override
    public boolean visit(OracleLobStorageClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleLobStorageClause x) {

    }

    @Override
    public boolean visit(OracleUnique x) {
        return visit((SQLUnique) x);
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return visit((SQLForeignKeyImpl) x);
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        return visit((SQLCheck) x);
    }

    @Override
    public void endVisit(OracleCheck x) {

    }

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalIdKey x) {

    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        return true;
    }

    @Override
    public void endVisit(OracleSupplementalLogGrp x) {

    }

    public boolean visit(OracleCreateTableStatement.Organization x) {
        return false;
    }

    public void endVisit(OracleCreateTableStatement.Organization x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement.OIDIndex x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement.OIDIndex x) {

    }

    @Override
    public boolean visit(OracleCreatePackageStatement x) {
        if (repository != null
                && x.getParent() == null) {
            repository.resolve(x);
        }

        for (SQLStatement stmt : x.getStatements()) {
            stmt.accept(this);
        }
        return false;
    }

    @Override
    public void endVisit(OracleCreatePackageStatement x) {

    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        SQLExpr dynamicSql = x.getDynamicSql();

        String sql = null;

        if (dynamicSql instanceof SQLIdentifierExpr) {
            String varName = ((SQLIdentifierExpr) dynamicSql).getName();

            SQLExpr valueExpr = null;
            if (x.getParent() instanceof SQLBlockStatement) {
                List<SQLStatement> statementList = ((SQLBlockStatement) x.getParent()).getStatementList();
                for (int i = 0, size = statementList.size(); i < size; ++i) {
                    SQLStatement stmt = statementList.get(i);
                    if (stmt == x) {
                        break;
                    }

                    if (stmt instanceof SQLSetStatement) {
                        List<SQLAssignItem> items = ((SQLSetStatement) stmt).getItems();
                        for (SQLAssignItem item : items) {
                            if (item.getTarget().equals(dynamicSql)) {
                                valueExpr = item.getValue();
                                break;
                            }
                        }
                    }
                }
            }

            if (valueExpr != null) {
                dynamicSql = valueExpr;
            }
        }

        if (dynamicSql instanceof SQLCharExpr) {
            sql = ((SQLCharExpr) dynamicSql).getText();
        }

        if (sql != null) {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
            for (SQLStatement stmt : stmtList) {
                stmt.accept(this);
            }
        }
        return false;
    }

    @Override
    public void endVisit(OracleExecuteImmediateStatement x) {

    }

    @Override
    public boolean visit(OracleTreatExpr x) {
        return true;
    }

    @Override
    public void endVisit(OracleTreatExpr x) {

    }

    @Override
    public boolean visit(OracleCreateSynonymStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateSynonymStatement x) {

    }

    @Override
    public boolean visit(OracleCreateTypeStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateTypeStatement x) {

    }

    @Override
    public boolean visit(OraclePipeRowStatement x) {
        return false;
    }

    @Override
    public void endVisit(OraclePipeRowStatement x) {

    }

    @Override
    public boolean visit(OracleIsOfTypeExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleIsOfTypeExpr x) {

    }

    @Override
    public boolean visit(OracleRunStatement x) {
        return true;
    }

    @Override
    public void endVisit(OracleRunStatement x) {

    }
}
