package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dbd_clause_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dbd_clause_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dbd_clause_assign_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dbd_clause_assign", remarks: "工作台条目分配租户") {
            column(name: "clause_assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "clause_id", type: "bigint",  remarks: "条目ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"clause_id,tenant_id",tableName:"hpfm_dbd_clause_assign",constraintName: "hpfm_dbd_clause_assign_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-05-08-hpfm_dbd_clause_assign") {
        addDefaultValue(tableName: "hpfm_dbd_clause_assign", columnName: "tenant_id", defaultValue: "0")
    }
}