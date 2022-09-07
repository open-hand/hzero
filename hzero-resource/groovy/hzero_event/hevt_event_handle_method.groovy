package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event_handle_method.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event_handle_method") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_handle_method_s', startValue:"1")
        }
        createTable(tableName: "hevt_event_handle_method", remarks: "事件处理类") {
            column(name: "event_handle_method_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "event_handle_service_id", type: "bigint",  remarks: "事件处理服务")  {constraints(nullable:"false")}  
            column(name: "handle_function", type: "varchar(" + 520 * weight + ")",  remarks: "事件处理方法")  {constraints(nullable:"false")}  
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"GLOBAL",   remarks: "层级，HEVT.EVENT_LEVEL_CODE,GLOBAL/TENANT")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",   defaultValue:"1",   remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}