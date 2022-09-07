package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_event.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_event") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_event_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_event", remarks: "告警事件") {
            column(name: "alert_event_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_id", type: "bigint",  remarks: "告警配置ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "data_source", type: "varchar(" + 255 * weight + ")",  remarks: "数据来源")   
            column(name: "alert_inst_code", type: "varchar(" + 255 * weight + ")",  remarks: "预警配置关联的实例编码")   
            column(name: "alert_range_code", type: "varchar(" + 20 * weight + ")",  remarks: "预警等级编码，值集编码：HALT.ALERT_RANGE")   
            column(name: "alert_time", type: "datetime",  remarks: "告警时间")   
            column(name: "processed_time", type: "datetime",  remarks: "完成时间")   
            column(name: "recovered_flag", type: "tinyint",  remarks: "是否恢复")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "halt_alert_event", indexName: "halt_alert_event_n1") {
            column(name: "alert_id")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "xingxing.wu@hand-china.com", id: "halt_alert_event-2020-12-23") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn (tableName: "halt_alert_event"){
            column (name: "event_unique_key", type: "varchar(" + 255* weight + ")", remarks: "事件标识")
        }
    }
    changeSet(author: "guoqiang.lv@hand-china.com", id: "halt_alert_event-2021-01-20") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        renameColumn(tableName: "halt_alert_event", oldColumnName: "event_unique_key",
                newColumnName: "source_label_key", columnDataType: "varchar(" + 255 * weight + ")", remarks: "标签标识")
    }
}