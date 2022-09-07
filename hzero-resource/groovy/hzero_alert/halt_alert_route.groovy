package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_route.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_route") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_route_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_route", remarks: "告警路由") {
            column(name: "alert_route_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_route_code", type: "varchar(" + 30 * weight + ")",  remarks: "告警代码，大写字母、数字、下划线、中划线")  {constraints(nullable:"false")}  
            column(name: "send_config_id", type: "bigint",  remarks: "发送配置ID，LOV编码HALT.ALERT_SEND_CONFIG")  {constraints(nullable:"false")}  
            column(name: "receiver_group_id", type: "bigint",  remarks: "接收组ID，LOV编码HALT.ALERT_RECEIVER_GROUP")  {constraints(nullable:"false")}  
            column(name: "continue_flag", type: "tinyint",   defaultValue:"0",   remarks: "命中后是否继续，默认不继续")  {constraints(nullable:"false")}  
            column(name: "group_by", type: "varchar(" + 255 * weight + ")",  remarks: "分组标签，逗号分隔")   
            column(name: "group_wait", type: "int",  remarks: "等待分组间隔时间，N秒")   
            column(name: "group_interval", type: "int",  remarks: "分组发送间隔时间，N分钟")   
            column(name: "repeat_interval", type: "int",  remarks: "重复发送间隔时间，N小时")   
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "说明")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "parent_id", type: "bigint",  remarks: "父级路由ID")   
            column(name: "level_path", type: "varchar(" + 600 * weight + ")",  remarks: "层级路径")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_route_code,tenant_id",tableName:"halt_alert_route",constraintName: "halt_alert_route_u1")
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "halt_alert_route-2020-12-23") {
        addColumn (tableName: "halt_alert_route"){
            column (name: "send_resolved", type: "tinyint(1)", remarks: "发送告警恢复", defaultValue: "1"){
                constraints (nullable: "false")
            }
        }
    }
}