package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_statistics.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_interface_statistics") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_statistics_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_statistics", remarks: "接口运维统计") {
            column(name: "interface_statistics_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "statistics_type", type: "varchar(" + 30 * weight + ")",  remarks: "统计类型，代码HITF.STATISTICS_TYPE")  {constraints(nullable:"false")}  
            column(name: "statistics_level", type: "varchar(" + 30 * weight + ")",  remarks: "统计维度，代码HITF.STATISTICS_LEVEL")   
            column(name: "statistics_level_value", type: "varchar(" + 128 * weight + ")",  remarks: "统计维度值")   
            column(name: "statistics_value", type: "int",  remarks: "统计值")  {constraints(nullable:"false")}  
            column(name: "statistics_time", type: "datetime",  remarks: "统计时间")  {constraints(nullable:"false")}  
            column(name: "statistics_time_value", type: "bigint",  remarks: "统计时间UTC毫秒值")  {constraints(nullable:"false")}  
            column(name: "statistics_detail", type: "longtext",  remarks: "统计明细")   
            column(name: "statistics_status", type: "varchar(" + 30 * weight + ")",  remarks: "统计状态，S/F")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_interface_statistics", indexName: "hitf_interface_statistics_n1") {
            column(name: "statistics_type")
            column(name: "statistics_level")
            column(name: "statistics_time")
        }

    }
}