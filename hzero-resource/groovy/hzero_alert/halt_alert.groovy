package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_s', startValue:"1")
        }
        createTable(tableName: "halt_alert", remarks: "告警配置") {
            column(name: "alert_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "alert_code", type: "varchar(" + 30 * weight + ")",  remarks: "告警代码，大写字母、数字、下划线、中划线")  {constraints(nullable:"false")}  
            column(name: "alert_name", type: "varchar(" + 255 * weight + ")",  remarks: "告警名称")  {constraints(nullable:"false")}  
            column(name: "alert_level", type: "varchar(" + 30 * weight + ")",  remarks: "告警级别，快速编码HALT.ALERT_LEVEL")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "说明")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_code,tenant_id",tableName:"halt_alert",constraintName: "halt_alert_u1")
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-07-015-halt_alert") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        modifyDataType(tableName: "halt_alert", columnName: 'alert_code', newDataType: "varchar(" + 120 * weight + ")")
    }
}