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

import org.hzero.starter.sqlparser.sql.ast.expr.SQLNumericLiteralExpr;
import org.hzero.starter.sqlparser.sql.dialect.oracle.visitor.OracleASTVisitor;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;

public class OracleBinaryDoubleExpr extends SQLNumericLiteralExpr implements OracleExpr {

    private Double value;

    public OracleBinaryDoubleExpr(){

    }

    public OracleBinaryDoubleExpr(Double value){
        super();
        this.value = value;
    }

    @Override
    public Number getNumber() {
        return value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OracleBinaryDoubleExpr other = (OracleBinaryDoubleExpr) obj;
        if (value == null) {
            return other.value == null;
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public void setNumber(Number number) {
        if (number == null) {
            this.setValue(null);
            return;
        }

        this.setValue(number.doubleValue());
    }

    public OracleBinaryDoubleExpr clone() {
        return new OracleBinaryDoubleExpr(value);
    }
}
