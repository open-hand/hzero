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
package org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.stmt;

import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLRollbackStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.ast.SQLServerStatement;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;

public class SQLServerRollbackStatement extends SQLRollbackStatement implements SQLServerStatement {

    private boolean work = false;

    private SQLExpr name;

    public boolean isWork() {
        return work;
    }

    public void setWork(boolean work) {
        this.work = work;
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(SQLServerASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public SQLExpr getName() {
        return name;
    }

    public void setName(SQLExpr name) {
        this.name = name;
    }

}
