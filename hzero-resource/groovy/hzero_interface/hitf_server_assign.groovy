package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_server_assign.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-28-hitf_server_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_server_assign_s', startValue:"1")
        }
        createTable(tableName: "hitf_server_assign", remarks: "应用与服务关联") {
            column(name: "assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键主键")  {constraints(primaryKey: true)} 
            column(name: "application_id", type: "bigint",  remarks: "应用ID，hitf_application.application_id")  {constraints(nullable:"false")}  
            column(name: "interface_server_id", type: "bigint",  remarks: "服务ID，hitf_interface_server.interface_server_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户Id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_server_assign", indexName: "hitf_server_assign_n1") {
            column(name: "application_id")
            column(name: "interface_server_id")
        }

    }
}