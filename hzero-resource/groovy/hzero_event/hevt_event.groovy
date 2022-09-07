package script.db

databaseChangeLog(logicalFilePath: 'script/db/hevt_event.groovy') {
    changeSet(author: "jian.zhang02@hand-china.com", id: "2020-06-30-hevt_event") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hevt_event_s', startValue:"1")
        }
        createTable(tableName: "hevt_event", remarks: "平台事件") {
            column(name: "event_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "category_id", type: "bigint",  remarks: "事件类型id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "event_code", type: "varchar(" + 30 * weight + ")",  remarks: "事件代码")  {constraints(nullable:"false")}  
            column(name: "event_name", type: "varchar(" + 360 * weight + ")",  remarks: "事件名称")   
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",  remarks: "层级，HEVT.EVENT_LEVEL_CODE,GLOBAL/TENANT")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "event_source_id", type: "bigint",  remarks: "事件源ID")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hevt_event", indexName: "opadm_event_n1") {
            column(name: "category_id")
        }

        addUniqueConstraint(columnNames:"event_code,tenant_id",tableName:"hevt_event",constraintName: "opadm_event_u1")
    }
}