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
package org.hzero.starter.sqlparser.sql.dialect.postgresql.visitor;

import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGBoxExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGCidrExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGCircleExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGExtractExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGInetExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGLineSegmentsExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGPointExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGPolygonExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.expr.PGTypeCastExpr;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGConnectToStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGFunctionTableSource;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.WindowClause;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGShowStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGStartTransactionStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import org.hzero.starter.sqlparser.sql.dialect.postgresql.ast.stmt.PGValuesQuery;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitorAdapter;

public class PGASTVisitorAdapter extends SQLASTVisitorAdapter implements PGASTVisitor {

    @Override
    public void endVisit(WindowClause x) {

    }

    @Override
    public boolean visit(WindowClause x) {

        return true;
    }

    @Override
    public void endVisit(FetchClause x) {

    }

    @Override
    public boolean visit(FetchClause x) {

        return true;
    }

    @Override
    public void endVisit(ForClause x) {

    }

    @Override
    public boolean visit(ForClause x) {

        return true;
    }

    @Override
    public void endVisit(PGDeleteStatement x) {

    }

    @Override
    public boolean visit(PGDeleteStatement x) {

        return true;
    }

    @Override
    public void endVisit(PGInsertStatement x) {

    }

    @Override
    public boolean visit(PGInsertStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }

    @Override
    public boolean visit(PGSelectStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGUpdateStatement x) {

    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGSelectQueryBlock x) {

    }

    @Override
    public boolean visit(PGSelectQueryBlock x) {
        return true;
    }

    @Override
    public void endVisit(PGFunctionTableSource x) {

    }

    @Override
    public boolean visit(PGFunctionTableSource x) {
        return true;
    }
	
	@Override
	public boolean visit(PGTypeCastExpr x) {
	    return true;
	}
	
	@Override
	public void endVisit(PGTypeCastExpr x) {
	    
	}

    @Override
    public void endVisit(PGValuesQuery x) {
        
    }

    @Override
    public boolean visit(PGValuesQuery x) {
        return true;
    }
    
    @Override
    public void endVisit(PGExtractExpr x) {
        
    }
    
    @Override
    public boolean visit(PGExtractExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGBoxExpr x) {
        
    }
    
    @Override
    public boolean visit(PGBoxExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGPointExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPointExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGMacAddrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGMacAddrExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGInetExpr x) {
        
    }
    
    @Override
    public boolean visit(PGInetExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGCidrExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCidrExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGPolygonExpr x) {
        
    }
    
    @Override
    public boolean visit(PGPolygonExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGCircleExpr x) {
        
    }
    
    @Override
    public boolean visit(PGCircleExpr x) {
        return true;
    }
    
    @Override
    public void endVisit(PGLineSegmentsExpr x) {
        
    }
    
    @Override
    public boolean visit(PGLineSegmentsExpr x) {
        return true;
    }

    @Override
    public void endVisit(PGShowStatement x) {
        
    }
    
    @Override
    public boolean visit(PGShowStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGStartTransactionStatement x) {
        
    }

    @Override
    public boolean visit(PGStartTransactionStatement x) {
        return true;
    }

    @Override
    public void endVisit(PGConnectToStatement x) {

    }

    @Override
    public boolean visit(PGConnectToStatement x) {
        return true;
    }

}
