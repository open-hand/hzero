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
package org.hzero.starter.sqlparser.sql.dialect.sqlserver.visitor;

import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.SQLServerOutput;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.SQLServerTop;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.expr.SQLServerObjectReferenceExpr;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement.SQLServerParameter;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerRollbackStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerSetTransactionIsolationLevelStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt.SQLServerWaitForStatement;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;

public interface SQLServerASTVisitor extends SQLASTVisitor {

    boolean visit(SQLServerSelectQueryBlock x);

    void endVisit(SQLServerSelectQueryBlock x);

    boolean visit(SQLServerTop x);

    void endVisit(SQLServerTop x);
    
    boolean visit(SQLServerObjectReferenceExpr x);
    
    void endVisit(SQLServerObjectReferenceExpr x);
    
    boolean visit(SQLServerInsertStatement x);
    
    void endVisit(SQLServerInsertStatement x);

    boolean visit(SQLServerUpdateStatement x);
    
    void endVisit(SQLServerUpdateStatement x);
    
    boolean visit(SQLServerExecStatement x);
    
    void endVisit(SQLServerExecStatement x);
    
    boolean visit(SQLServerSetTransactionIsolationLevelStatement x);

    void endVisit(SQLServerSetTransactionIsolationLevelStatement x);

    boolean visit(SQLServerOutput x);

    void endVisit(SQLServerOutput x);

    boolean visit(SQLServerRollbackStatement x);

    void endVisit(SQLServerRollbackStatement x);
    
    boolean visit(SQLServerWaitForStatement x);

    void endVisit(SQLServerWaitForStatement x);
    
    boolean visit(SQLServerParameter x);

    void endVisit(SQLServerParameter x);

}
