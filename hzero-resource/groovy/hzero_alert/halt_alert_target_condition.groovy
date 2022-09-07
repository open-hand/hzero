package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_target_condition.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-10-26-halt_alert_target_condition") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_target_condition_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_target_condition", remarks: "告警目标条件") {
            column(name: "alert_target_condition_id", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "alert_target_id", type: "bigint(20)",  remarks: "告警目标ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint(20)",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "condition_name", type: "varchar(" + 30 * weight + ")",  remarks: "条件名称")  {constraints(nullable:"false")}  
            column(name: "condition_order", type: "int(11)",   defaultValue:"0",   remarks: "条件顺序")  {constraints(nullable:"false")}  
            column(name: "alert_target_type", type: "varchar(" + 60 * weight + ")",  remarks: "目标类型代码，值集:HALT.ALERT_TARGET_TYPE MSG|MQ|API")   
            column(name: "mq_topic", type: "varchar(" + 150 * weight + ")",  remarks: "消息中间件的回调topic")   
            column(name: "api_callback", type: "varchar(" + 150 * weight + ")",  remarks: "API回调路径")   
            column(name: "api_sign_key", type: "varchar(" + 150 * weight + ")",  remarks: "API签名key")   
            column(name: "msg_send_config_code", type: "varchar(" + 60 * weight + ")",  remarks: "消息编码,hmsg_template_server.message_code")   
            column(name: "msg_receiver_code", type: "varchar(" + 60 * weight + ")",  remarks: "接收组编码，hmsg_receiver_type.type_code")   
            column(name: "object_version_number", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint(20)",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}