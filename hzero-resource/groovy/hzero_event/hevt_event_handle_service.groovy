package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_handle_service.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_handle_service") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_handle_service_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_handle_service", remarks: "事件处理服务") {
            column(name: "event_handle_service_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "event_id", type: "bigint",   defaultValue:"0",   remarks: "事件ID")  {constraints(nullable:"false")}  
            column(name: "service_code", type: "varchar(" + 60 * weight + ")",  remarks: "事件处理服务编码")  {constraints(nullable:"false")}  
            column(name: "group_id", type: "varchar(" + 60 * weight + ")",  remarks: "事件服务处理分组")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hevt_event_handle_service", indexName: "opadm_event_handle_service_u1") {
            column(name: "service_code")
            column(name: "event_id")
        }

    }
}