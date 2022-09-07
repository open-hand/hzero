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

import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.SQLName;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLTableSource;
import org.hzero.starter.sqlparser.sql.dialect.odps.visitor.OdpsASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OdpsSetLabelStatement extends OdpsStatementImpl {

    private String         label;

    private SQLExpr        user;

    private SQLTableSource table;

    private List<SQLName>  columns = new ArrayList<SQLName>();

    @Override
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, user);
        }
        visitor.endVisit(this);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SQLExpr getUser() {
        return user;
    }

    public void setUser(SQLExpr user) {
        this.user = user;
        user.setParent(this);
    }

    public SQLTableSource getTable() {
        return table;
    }

    public void setTable(SQLTableSource table) {
        this.table = table;
        table.setParent(this);
    }

    public List<SQLName> getColumns() {
        return columns;
    }

}
