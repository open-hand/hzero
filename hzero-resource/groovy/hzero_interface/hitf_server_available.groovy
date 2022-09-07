package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_server_available.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-18-hitf_server_available") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_server_available_s', startValue: "1")
        }
        createTable(tableName: "hitf_server_available", remarks: "用户可用服务信息") {
            column(name: "server_available_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "user_purchase_id", type: "bigint", remarks: "表hitf_user_purchase主键") { constraints(nullable: "false") }
            column(name: "status_code", type: "varchar(" + 30 + ")", remarks: "状态") { constraints(nullable: "false") }
            column(name: "server_type_code", type: "varchar(" + 30 + ")", remarks: "类型：接口、服务，值集:HCHG.CHARGE_TYPE") { constraints(nullable: "false") }
            column(name: "interface_server_id", type: "bigint", remarks: "表hitf_interface_server主键ID") { constraints(nullable: "false") }
            column(name: "interface_id", type: "bigint", remarks: "表hitf_interface主键ID")
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "remark", type: "varchar(" + 360 * weight + ")", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hitf_server_available", indexName: "hitf_server_available_n1") {
            column(name: "interface_server_id")
        }
        createIndex(tableName: "hitf_server_available", indexName: "hitf_server_available_n2") {
            column(name: "interface_id")
        }
        createIndex(tableName: "hitf_server_available", indexName: "hitf_server_available_n3") {
            column(name: "user_purchase_id")
        }
    }
}