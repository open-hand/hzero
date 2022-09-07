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
package org.hzero.starter.sqlparser.sql.ast.expr;

import org.hzero.starter.sqlparser.sql.SQLUtils;
import org.hzero.starter.sqlparser.sql.ast.SQLDataType;
import org.hzero.starter.sqlparser.sql.ast.SQLDeclareItem;
import org.hzero.starter.sqlparser.sql.ast.SQLExpr;
import org.hzero.starter.sqlparser.sql.ast.SQLExprImpl;
import org.hzero.starter.sqlparser.sql.ast.SQLName;
import org.hzero.starter.sqlparser.sql.ast.SQLObject;
import org.hzero.starter.sqlparser.sql.ast.SQLParameter;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLColumnDefinition;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelect;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelectItem;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelectQueryBlock;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSubqueryTableSource;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLTableSource;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;
import org.hzero.starter.sqlparser.util.FnvHash;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SQLIdentifierExpr extends SQLExprImpl implements SQLName {
    protected String    name;
    private   long      hashCode64;

    private   SQLObject resolvedColumn;
    private   SQLObject resolvedOwnerObject;

    public SQLIdentifierExpr(){

    }

    public SQLIdentifierExpr(String name){
        this.name = name;
    }

    public SQLIdentifierExpr(String name, long hash_lower){
        this.name = name;
        this.hashCode64 = hash_lower;
    }

    public String getSimpleName() {
        return name;
    }

    public String getLowerName() {
        if (name == null) {
            return null;
        }

        return name.toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.hashCode64 = 0L;

        if (parent instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) parent;
            propertyExpr.computeHashCode64();
        }
    }

    public long nameHashCode64() {
        return hashCode64();
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0
                && name != null) {
            hashCode64 = FnvHash.hashCode64(name);
        }
        return hashCode64;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        long value = hashCode64();
        return (int)(value ^ (value >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SQLIdentifierExpr)) {
            return false;
        }

        SQLIdentifierExpr other = (SQLIdentifierExpr) obj;
        return this.hashCode64() == other.hashCode64();
    }

    public String toString() {
        return this.name;
    }

    public SQLIdentifierExpr clone() {
        SQLIdentifierExpr x = new SQLIdentifierExpr(this.name, hashCode64);
        x.resolvedColumn = resolvedColumn;
        x.resolvedOwnerObject = resolvedOwnerObject;
        return x;
    }

    public SQLIdentifierExpr simplify() {
        String normalized = SQLUtils.normalize(name);
        if (!Objects.equals(normalized, name)) {
           return new SQLIdentifierExpr(normalized, hashCode64);
        }
        return this;
    }

    public String normalizedName() {
        return SQLUtils.normalize(name);
    }

    public SQLColumnDefinition getResolvedColumn() {
        if (resolvedColumn instanceof SQLColumnDefinition) {
            return (SQLColumnDefinition) resolvedColumn;
        }

        return null;
    }

    public SQLObject getResolvedColumnObject() {
        return resolvedColumn;
    }

    public void setResolvedColumn(SQLColumnDefinition resolvedColumn) {
        this.resolvedColumn = resolvedColumn;
    }

    public SQLTableSource getResolvedTableSource() {
        if (resolvedOwnerObject instanceof SQLTableSource) {
            return (SQLTableSource) resolvedOwnerObject;
        }

        return null;
    }

    public void setResolvedTableSource(SQLTableSource resolvedTableSource) {
        this.resolvedOwnerObject = resolvedTableSource;
    }

    public SQLObject getResolvedOwnerObject() {
        return resolvedOwnerObject;
    }

    public void setResolvedOwnerObject(SQLObject resolvedOwnerObject) {
        this.resolvedOwnerObject = resolvedOwnerObject;
    }

    public SQLParameter getResolvedParameter() {
        if (resolvedColumn instanceof SQLParameter) {
            return (SQLParameter) this.resolvedColumn;
        }
        return null;
    }

    public void setResolvedParameter(SQLParameter resolvedParameter) {
        this.resolvedColumn = resolvedParameter;
    }

    public SQLDeclareItem getResolvedDeclareItem() {
        if (resolvedColumn instanceof SQLDeclareItem) {
            return (SQLDeclareItem) this.resolvedColumn;
        }
        return null;
    }

    public void setResolvedDeclareItem(SQLDeclareItem resolvedDeclareItem) {
        this.resolvedColumn = resolvedDeclareItem;
    }

    public SQLDataType computeDataType() {
        SQLColumnDefinition resolvedColumn = getResolvedColumn();
        if (resolvedColumn != null) {
            return resolvedColumn.getDataType();
        }

        if (resolvedOwnerObject != null
                && resolvedOwnerObject instanceof SQLSubqueryTableSource) {
            SQLSelect select = ((SQLSubqueryTableSource) resolvedOwnerObject).getSelect();
            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(nameHashCode64());
            if (selectItem != null) {
                return selectItem.computeDataType();
            }
        }

        return null;
    }

    public boolean nameEquals(String name) {
        return SQLUtils.nameEquals(this.name, name);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    public static boolean matchIgnoreCase(SQLExpr expr, String name) {
        if (!(expr instanceof SQLIdentifierExpr)) {
            return false;
        }
        SQLIdentifierExpr ident = (SQLIdentifierExpr) expr;
        return ident.getName().equalsIgnoreCase(name);
    }
}
