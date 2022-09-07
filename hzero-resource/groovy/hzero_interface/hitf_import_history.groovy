package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_import_history.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-11-04-hitf_import_history") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_import_history_s', startValue:"1")
        }
        createTable(tableName: "hitf_import_history", remarks: "服务导入历史") {
            column(name: "import_history_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "request_num", type: "varchar(" + 128 * weight + ")",  remarks: "请求编码，自动生成，UUID，作为检索条件")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "server_code", type: "varchar(" + 128 * weight + ")",  remarks: "导入服务编码")   
            column(name: "import_url", type: "varchar(" + 600 * weight + ")",  remarks: "导入地址")  {constraints(nullable:"false")}  
            column(name: "import_by", type: "bigint",  remarks: "提交人")  {constraints(nullable:"false")}  
            column(name: "import_status", type: "varchar(" + 30 * weight + ")",  remarks: "导入状态，HITF.IMPORT_STATUS，PENDING-待定/RUNNING-运行中/COMPLETE-完成/FAILED-失败")  {constraints(nullable:"false")}  
            column(name: "import_message", type: "longtext",  remarks: "导入消息")   
            column(name: "start_time", type: "datetime",  remarks: "开始时间")  {constraints(nullable:"false")}  
            column(name: "end_time", type: "datetime",  remarks: "结束时间")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_import_history", indexName: "hitf_import_history_n1") {
			column(name: "import_url")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"request_num",tableName:"hitf_import_history",constraintName: "hitf_import_history_u1")
    }
}