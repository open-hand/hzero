package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_source.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_source") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_source_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_source", remarks: "告警来源") {
            column(name: "alert_source_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_id", type: "bigint",  remarks: "告警ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "alert_source_type", type: "varchar(" + 30 * weight + ")",  remarks: "告警来源类型，HALT.ALERT_SOURCE_TYPE，DIRECT/DATASET/INFRASTRUCTURE/APPLICATION/LOGGING")
            column(name: "alert_rule_code", type: "varchar(" + 30 * weight + ")",  remarks: "告警规则代码")   
            column(name: "auto_recover_flag", type: "tinyint",   defaultValue:"0",   remarks: "告警恢复标识")  {constraints(nullable:"false")}  
            column(name: "upgrade_flag", type: "tinyint",   defaultValue:"0",   remarks: "告警升级")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "halt_alert_source", indexName: "halt_alert_source_n1") {
            column(name: "alert_source_type")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"alert_id",tableName:"halt_alert_source",constraintName: "halt_alert_source_u1")
    }
}