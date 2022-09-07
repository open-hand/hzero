package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_target.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_target") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_target_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_target", remarks: "告警目标") {
            column(name: "alert_target_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "alert_route_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"SIMPLE",   remarks: "路由方式，HALT.ALERT_ROUTE_TYPE SIMPLE/COMPLEX")  {constraints(nullable:"false")}  
            column(name: "alert_target_type", type: "varchar(" + 60 * weight + ")",  remarks: "目标类型代码，值集:HALT.ALERT_TARGET_TYPE MSG|MQ|API")   
            column(name: "mq_topic", type: "varchar(" + 150 * weight + ")",  remarks: "消息中间件的回调topic")   
            column(name: "api_callback", type: "varchar(" + 150 * weight + ")",  remarks: "API回调路径")   
            column(name: "api_sign_key", type: "varchar(" + 150 * weight + ")",  remarks: "API签名key")   
            column(name: "msg_send_config_code", type: "varchar(" + 60 * weight + ")",  remarks: "消息编码,hmsg_template_server.message_code")   
            column(name: "msg_receiver_code", type: "varchar(" + 60 * weight + ")",  remarks: "接收组编码，hmsg_receiver_type.type_code")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "halt_alert_target", indexName: "halt_alert_target_n1") {
            column(name: "alert_target_type")
        }

        addUniqueConstraint(columnNames:"alert_id",tableName:"halt_alert_target",constraintName: "halt_alert_target_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-07-30-halt_alert_target") {
        addColumn(tableName: 'halt_alert_target') {
            column(name: "continue_flag", type: "tinyint",   defaultValue:"0",   remarks: "命中后是否继续，默认不继续")  {constraints(nullable:"false")}
        }
    }
}