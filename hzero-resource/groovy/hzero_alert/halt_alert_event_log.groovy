package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_event_log.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_event_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_event_log_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_event_log", remarks: "告警事件日志") {
            column(name: "alert_event_log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_event_id", type: "bigint",  remarks: "告警事件ID")  {constraints(nullable:"false")}  
            column(name: "alert_id", type: "bigint",  remarks: "告警配置ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "process_stage", type: "varchar(" + 30 * weight + ")",  remarks: "预警流程阶段，值集：HALT.PROCESS_STAGE")  {constraints(nullable:"false")}  
            column(name: "process_status", type: "tinyint",   defaultValue:"1",   remarks: "流程阶段状态")  {constraints(nullable:"false")}  
            column(name: "process_data", type: "longtext",  remarks: "处理数据")   
            column(name: "process_message", type: "longtext",  remarks: "处理消息")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "halt_alert_event_log", indexName: "halt_alert_event_log_u1") {
            column(name: "alert_event_id")
            column(name: "alert_id")
            column(name: "tenant_id")
            column(name: "process_stage")
        }

    }

    changeSet(author: "xingxing.wu@hand-china.com", id: "halt_alert_event_log-2020-12-23") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn (tableName: "halt_alert_event_log"){
            column (name: "event_instance_id", type: "varchar(" + 255* weight + ")", remarks: "事件实例标识"){
                constraints (nullable: "false")
            }
        }
        createIndex (tableName: "halt_alert_event_log", indexName: "halt_alert_event_log_n1"){
            column (name: "event_instance_id")
        }
    }
}