package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_server_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-02-hpfm_server_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_server_assign_s', startValue:"1")
        }
        createTable(tableName: "hpfm_server_assign", remarks: "文件服务器的集群分配") {
            column(name: "ASSIGN_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "CLUSTER_ID", type:  "bigint",  remarks: "集群ID")  {constraints(nullable:"false")}  
            column(name: "SERVER_ID", type: "bigint",  remarks: "服务器ID")  {constraints(nullable:"false")}  
            column(name: "ASSIGN_DESCRIPTION", type: "varchar(" + 240 * weight + ")",  remarks: "分配描述")  
            column(name: "tenant_id", type: "bigint", defaultValue:"0",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}