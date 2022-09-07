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
package org.hzero.starter.sqlparser.sql.dialect.odps.ast;

import org.hzero.starter.sqlparser.sql.SQLUtils;
import org.hzero.starter.sqlparser.sql.ast.SQLLimit;
import org.hzero.starter.sqlparser.sql.ast.SQLOrderBy;
import org.hzero.starter.sqlparser.sql.ast.expr.SQLIntegerExpr;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelectOrderByItem;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.dialect.odps.visitor.OdpsASTVisitor;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;
import org.hzero.starter.sqlparser.util.JdbcConstants;

import java.util.ArrayList;

public class OdpsSelectQueryBlock extends SQLSelectQueryBlock {

    private SQLOrderBy orderBy;

    public OdpsSelectQueryBlock(){
        dbType = JdbcConstants.ODPS;

        distributeBy = new ArrayList<SQLSelectOrderByItem>();
        sortBy = new ArrayList<SQLSelectOrderByItem>(2);
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((limit == null) ? 0 : limit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        OdpsSelectQueryBlock other = (OdpsSelectQueryBlock) obj;
        if (limit == null) {
            if (other.limit != null) return false;
        } else if (!limit.equals(other.limit)) return false;
        return true;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OdpsASTVisitor) {
            accept0((OdpsASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    public void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.orderBy);
            acceptChild(visitor, this.distributeBy);
            acceptChild(visitor, this.sortBy);
            acceptChild(visitor, this.limit);
            acceptChild(visitor, this.into);
        }

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toOdpsString(this);
    }

    public void limit(int rowCount, int offset) {
        if (offset > 0) {
            throw new UnsupportedOperationException("not support offset");
        }

        setLimit(new SQLLimit(new SQLIntegerExpr(rowCount)));
    }
}
