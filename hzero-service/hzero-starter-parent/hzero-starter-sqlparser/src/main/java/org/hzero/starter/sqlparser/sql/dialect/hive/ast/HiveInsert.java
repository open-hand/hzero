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
package org.hzero.starter.sqlparser.sql.dialect.hive.ast;

import org.hzero.starter.sqlparser.sql.ast.SQLName;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLAssignItem;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLExprTableSource;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLInsertInto;
import org.hzero.starter.sqlparser.sql.ast.statement.SQLSelect;
import org.hzero.starter.sqlparser.sql.dialect.hive.visitor.HiveASTVisitor;
import org.hzero.starter.sqlparser.sql.dialect.odps.visitor.OdpsASTVisitor;
import org.hzero.starter.sqlparser.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class HiveInsert extends SQLInsertInto {

    private boolean              overwrite  = false;
    private List<SQLAssignItem>  partitions = new ArrayList<SQLAssignItem>();

    public HiveInsert() {

    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }
    
    public void addPartition(SQLAssignItem partition) {
        if (partition != null) {
            partition.setParent(this);
        }
        this.partitions.add(partition);
    }

    public void setPartitions(List<SQLAssignItem> partitions) {
        this.partitions = partitions;
    }

    public void cloneTo(HiveInsert x) {
        x.overwrite = overwrite;
        for (SQLAssignItem item : partitions) {
            x.addPartition(item.clone());
        }
    }

    @Override
    public SQLInsertInto clone() {
        HiveInsert x = new HiveInsert();
        cloneTo(x);
        return x;
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof HiveASTVisitor) {
            accept0((HiveASTVisitor) visitor);
        } else {
            accept0((OdpsASTVisitor) visitor);
        }
    }

    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }

    protected void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, partitions);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }
}
