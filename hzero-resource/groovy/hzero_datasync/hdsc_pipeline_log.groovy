package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_pipeline_log.groovy') {
    changeSet(author: "hzero", id: "2020-08-21-hdsc_pipeline_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hdsc_pipeline_log_s', startValue: "1")
        }
        createTable(tableName: "hdsc_pipeline_log", remarks: "") {
            column(name: "pipeline_log_id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "pipeline_code", type: "varchar(" + 60 * weight + ")", defaultValue: "", remarks: "关联hdsc_data_stream_pipeline.pipeline_code") { constraints(nullable: "false") }
            column(name: "namespace", type: "varchar(" + 60 * weight + ")", defaultValue: "", remarks: "")
            column(name: "log_level", type: "varchar(" + 30 * weight + ")", defaultValue: "", remarks: "日志级别，DEBUG/WARN/INFO/ERROR") { constraints(nullable: "false") }
            column(name: "log_content", type: "longtext", remarks: "")
            column(name: "created_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n1") {
            column(name: "namespace")
        }
        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n2") {
            column(name: "log_level")
        }
        createIndex(tableName: "hdsc_pipeline_log", indexName: "hdsc_pipeline_log_n3") {
            column(name: "pipeline_code")
        }

    }
}
