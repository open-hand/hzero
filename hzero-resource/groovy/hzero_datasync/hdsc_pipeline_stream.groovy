package script.db

databaseChangeLog(logicalFilePath: 'script/db/hdsc_pipeline_stream.groovy') {
    changeSet(author: "hzero", id: "2020-08-21-hdsc_pipeline_stream") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hdsc_pipeline_stream_s', startValue: "1")
        }
        createTable(tableName: "hdsc_pipeline_stream", remarks: "") {
            column(name: "pipeline_stream_id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "pipeline_id", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "stream_id", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "") { constraints(nullable: "false") }
            column(name: "prev_ps_id", type: "bigint", remarks: "")
            column(name: "next_ps_id", type: "bigint", remarks: "")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hdsc_pipeline_stream", indexName: "hdsc_pipeline_stream_n1") {
            column(name: "pipeline_id")
        }

    }
}
