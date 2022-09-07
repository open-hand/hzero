package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_inhibit.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_inhibit") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_inhibit_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_inhibit", remarks: "告警抑制") {
            column(name: "alert_inhibit_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "alert_inhibit_code", type: "varchar(" + 30 * weight + ")",  remarks: "告警抑制代码，大写字母、数字、下划线、中划线")  {constraints(nullable:"false")}  
            column(name: "alert_inhibit_name", type: "varchar(" + 255 * weight + ")",  remarks: "告警抑制名称")  {constraints(nullable:"false")}  
            column(name: "label_match", type: "varchar(" + 255 * weight + ")",  remarks: "标签匹配，逗号分隔字符串")  {constraints(nullable:"false")}  
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_inhibit_code,tenant_id",tableName:"halt_alert_inhibit",constraintName: "halt_alert_inhibit_u1")
    }
}