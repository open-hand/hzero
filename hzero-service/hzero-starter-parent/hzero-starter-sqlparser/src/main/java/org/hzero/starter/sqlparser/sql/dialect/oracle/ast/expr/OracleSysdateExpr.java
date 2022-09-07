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
package org.hzero.starter.sqlparser.sql.dialect.oracle.ast.expr;

import org.hzero.starter.sqlparser.sql.SQLUtils;
import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.SQLObject;
import org.hzero.starter.sqlparser.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import org.hzero.starter.sqlparser.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.Collections;
import java.util.List;

public class OracleSysdateExpr extends OracleSQLObjectImpl implements SQLExpr {

    private String option;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public OracleSysdateExpr clone() {
        OracleSysdateExpr x = new OracleSysdateExpr();
        x.option = option;
        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>emptyList();
    }

    public String toString() {
        return SQLUtils.toOracleString(this);
    }
}
