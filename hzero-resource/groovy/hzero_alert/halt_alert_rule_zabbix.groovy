package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_rule_zabbix.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_rule_zabbix") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_rule_zabbix_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_rule_zabbix", remarks: "告警触发规则-Zabbix") {
            column(name: "alert_rule_zabbix_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_source_id", type: "bigint",  remarks: "告警来源ID")  {constraints(nullable:"false")}  
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "rule_target_type", type: "varchar(" + 30 * weight + ")",  remarks: "基础告警规则应用目标类型，快速编码HALT.ALERT_ZABBIX_TARGET")  {constraints(nullable:"false")}  
            column(name: "zabbix_host_id", type: "bigint",  remarks: "规则应用目标id")  {constraints(nullable:"false")}  
            column(name: "zabbix_trigger_id", type: "bigint",  remarks: "触发器ID")  {constraints(nullable:"false")}  
            column(name: "alert_expression", type: "longtext",  remarks: "告警表达式")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_id,tenant_id",tableName:"halt_alert_rule_zabbix",constraintName: "halt_alert_rule_zabbix_u1")
    }

    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-10-23-halt_alert_rule_zabbix"){
        dropTable(tableName: "halt_alert_rule_zabbix")
    }
}