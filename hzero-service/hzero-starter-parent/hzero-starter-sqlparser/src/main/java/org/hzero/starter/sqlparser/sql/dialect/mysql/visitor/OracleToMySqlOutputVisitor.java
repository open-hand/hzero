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
package org.hzero.starter.sqlparser.sql.dialect.mysql.visitor;

import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.OracleDataTypeIntervalDay;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.OracleDataTypeIntervalYear;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.CycleClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.clause.ModelClause;
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
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OraclePipeRowStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OraclePrimaryKey;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleRaiseStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleRunStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectJoin;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectPivot;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectRestriction;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectTableReference;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSelectUnPivot;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUnique;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.stmt.OracleUsingIndexClause;
import org.hzero.starter.sqlparser.sql.dialect.oracle.visitor.OracleASTVisitor;

/**
 * Created by wenshao on 16/07/2017.
 */
public class OracleToMySqlOutputVisitor extends MySqlOutputVisitor implements OracleASTVisitor {
    public OracleToMySqlOutputVisitor(Appendable appender) {
        super(appender);
    }

    public OracleToMySqlOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
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
    public void endVisit(OracleDeleteStatement x) {

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
    public void endVisit(OracleSelectPivot.Item x) {

    }

    @Override
    public void endVisit(OracleSelectRestriction.CheckOption x) {

    }

    @Override
    public void endVisit(OracleSelectRestriction.ReadOnly x) {

    }

    @Override
    public void endVisit(OracleSelectSubqueryTableSource x) {

    }

    @Override
    public void endVisit(OracleSelectUnPivot x) {

    }

    @Override
    public void endVisit(OracleUpdateStatement x) {

    }

    @Override
    public boolean visit(OracleAnalytic x) {
        return false;
    }

    @Override
    public boolean visit(OracleAnalyticWindowing x) {
        return false;
    }

    @Override
    public boolean visit(OracleDbLinkExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleDeleteStatement x) {
        return false;
    }

    @Override
    public boolean visit(OracleIntervalExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleOuterExpr x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectJoin x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectPivot x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectPivot.Item x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectRestriction.CheckOption x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectRestriction.ReadOnly x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectSubqueryTableSource x) {
        return false;
    }

    @Override
    public boolean visit(OracleSelectUnPivot x) {
        return false;
    }

    @Override
    public boolean visit(OracleUpdateStatement x) {
        return false;
    }

    @Override
    public boolean visit(SampleClause x) {
        return false;
    }

    @Override
    public void endVisit(SampleClause x) {

    }

    @Override
    public boolean visit(OracleSelectTableReference x) {
        return false;
    }

    @Override
    public void endVisit(OracleSelectTableReference x) {

    }

    @Override
    public boolean visit(PartitionExtensionClause x) {
        return false;
    }

    @Override
    public void endVisit(PartitionExtensionClause x) {

    }

    @Override
    public boolean visit(OracleWithSubqueryEntry x) {
        return false;
    }

    @Override
    public void endVisit(OracleWithSubqueryEntry x) {

    }

    @Override
    public boolean visit(SearchClause x) {
        return false;
    }

    @Override
    public void endVisit(SearchClause x) {

    }

    @Override
    public boolean visit(CycleClause x) {
        return false;
    }

    @Override
    public void endVisit(CycleClause x) {

    }

    @Override
    public boolean visit(OracleBinaryFloatExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleBinaryFloatExpr x) {

    }

    @Override
    public boolean visit(OracleBinaryDoubleExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleBinaryDoubleExpr x) {

    }

    @Override
    public boolean visit(OracleCursorExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleCursorExpr x) {

    }

    @Override
    public boolean visit(OracleIsSetExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleIsSetExpr x) {

    }

    @Override
    public boolean visit(ModelClause.ReturnRowsClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.ReturnRowsClause x) {

    }

    @Override
    public boolean visit(ModelClause.MainModelClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.MainModelClause x) {

    }

    @Override
    public boolean visit(ModelClause.ModelColumnClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.ModelColumnClause x) {

    }

    @Override
    public boolean visit(ModelClause.QueryPartitionClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.QueryPartitionClause x) {

    }

    @Override
    public boolean visit(ModelClause.ModelColumn x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.ModelColumn x) {

    }

    @Override
    public boolean visit(ModelClause.ModelRulesClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.ModelRulesClause x) {

    }

    @Override
    public boolean visit(ModelClause.CellAssignmentItem x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.CellAssignmentItem x) {

    }

    @Override
    public boolean visit(ModelClause.CellAssignment x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause.CellAssignment x) {

    }

    @Override
    public boolean visit(ModelClause x) {
        return false;
    }

    @Override
    public void endVisit(ModelClause x) {

    }

    @Override
    public boolean visit(OracleReturningClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleReturningClause x) {

    }

    @Override
    public boolean visit(OracleInsertStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleInsertStatement x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.InsertIntoClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.InsertIntoClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClause x) {
        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.ConditionalInsertClause x) {

    }

    @Override
    public boolean visit(OracleMultiInsertStatement.ConditionalInsertClauseItem x) {
        return false;
    }

    @Override
    public void endVisit(OracleMultiInsertStatement.ConditionalInsertClauseItem x) {

    }

    @Override
    public boolean visit(OracleSelectQueryBlock x) {
        return false;
    }

    @Override
    public void endVisit(OracleSelectQueryBlock x) {

    }

    @Override
    public boolean visit(OracleLockTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleLockTableStatement x) {

    }

    @Override
    public boolean visit(OracleAlterSessionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterSessionStatement x) {

    }

    @Override
    public boolean visit(OracleDatetimeExpr x) {
        return false;
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
    public boolean visit(OracleExceptionStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExceptionStatement x) {

    }

    @Override
    public boolean visit(OracleExceptionStatement.Item x) {
        return false;
    }

    @Override
    public void endVisit(OracleExceptionStatement.Item x) {

    }

    @Override
    public boolean visit(OracleArgumentExpr x) {
        return false;
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
        return false;
    }

    @Override
    public void endVisit(OracleAlterTableModify x) {

    }

    @Override
    public boolean visit(OracleCreateIndexStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateIndexStatement x) {

    }

    @Override
    public boolean visit(OracleForStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleForStatement x) {

    }

    @Override
    public boolean visit(OracleRangeExpr x) {
        return false;
    }

    @Override
    public void endVisit(OracleRangeExpr x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement x) {

    }

    @Override
    public boolean visit(OraclePrimaryKey x) {
        return false;
    }

    @Override
    public void endVisit(OraclePrimaryKey x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleCreateTableStatement x) {

    }

    @Override
    public boolean visit(OracleAlterIndexStatement.Rebuild x) {
        return false;
    }

    @Override
    public void endVisit(OracleAlterIndexStatement.Rebuild x) {

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
        return false;
    }

    @Override
    public void endVisit(OracleUnique x) {

    }

    @Override
    public boolean visit(OracleForeignKey x) {
        return false;
    }

    @Override
    public void endVisit(OracleForeignKey x) {

    }

    @Override
    public boolean visit(OracleCheck x) {
        return false;
    }

    @Override
    public void endVisit(OracleCheck x) {

    }

    @Override
    public boolean visit(OracleSupplementalIdKey x) {
        return false;
    }

    @Override
    public void endVisit(OracleSupplementalIdKey x) {

    }

    @Override
    public boolean visit(OracleSupplementalLogGrp x) {
        return false;
    }

    @Override
    public void endVisit(OracleSupplementalLogGrp x) {

    }

    @Override
    public boolean visit(OracleCreateTableStatement.Organization x) {
        return false;
    }

    @Override
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
        return false;
    }

    @Override
    public void endVisit(OracleCreatePackageStatement x) {

    }

    @Override
    public boolean visit(OracleExecuteImmediateStatement x) {
        return false;
    }

    @Override
    public void endVisit(OracleExecuteImmediateStatement x) {

    }

    @Override
    public boolean visit(OracleTreatExpr x) {
        return false;
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
        return false;
    }

    @Override
    public void endVisit(OracleRunStatement x) {

    }
}
