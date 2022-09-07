package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_silence.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_silence") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_silence_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_silence", remarks: "告警静默") {
            column(name: "alert_silence_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "alert_silence_code", type: "varchar(" + 30 * weight + ")",  remarks: "告警静默代码，大写字母、数字、下划线、中划线")  {constraints(nullable:"false")}  
            column(name: "alert_silence_name", type: "varchar(" + 255 * weight + ")",  remarks: "告警静默名称")  {constraints(nullable:"false")}  
            column(name: "start_time", type: "datetime",  remarks: "开始时间")   
            column(name: "end_time", type: "datetime",  remarks: "结束时间")   
            column(name: "remark", type: "varchar(" + 255 * weight + ")",  remarks: "备注/原因/记录等")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"alert_silence_code,tenant_id",tableName:"halt_alert_silence",constraintName: "halt_alert_silence_u1")
    }
}