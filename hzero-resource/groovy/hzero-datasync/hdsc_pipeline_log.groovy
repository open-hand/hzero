package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_pipeline_log.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-01-hdsc_pipeline_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hdsc_pipeline_log_s', startValue:"1")
        }
        createTable(tableName: "hdsc_pipeline_log", remarks: "数据流") {
            column(name: "pipeline_log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "pipeline_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据流管道编码")  {constraints(nullable:"false")}
            column(name: "namespace", type: "varchar(" + 60 * weight + ")",  remarks: "命名空间")  {constraints(nullable:"true")}
            column(name: "log_level", type: "varchar(" + 30 * weight + ")",  remarks: "日志级别(ERROR|INFO|WARN|DEBUG)")  {constraints(nullable:"false")}
            column(name: "log_content", type: "longtext",  remarks: "日志内容")  {constraints(nullable:"true")}
            column(name: "created_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n1") {
            column(name: "pipeline_code")
        }
        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n2") {
            column(name: "namespace")
        }
        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n3") {
            column(name: "log_level")
        }
    }
}