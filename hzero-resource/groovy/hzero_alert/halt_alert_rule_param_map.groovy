package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_rule_param_map.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_rule_param_map") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_rule_param_map_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_rule_param_map", remarks: "告警规则参数映射") {
            column(name: "alert_rule_param_map_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_source_id", type: "bigint",  remarks: "告警来源ID")  {constraints(nullable:"false")}  
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "source_key", type: "varchar(" + 60 * weight + ")",  remarks: "来源标识")   
            column(name: "target_key", type: "varchar(" + 60 * weight + ")",  remarks: "目标标识")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        createIndex(tableName: "halt_alert_rule_param_map", indexName: "halt_alert_rule_param_map_n1") {
            column(name: "alert_id")
        }
    }
}