package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approve_chain_json.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approve_chain_json") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approve_chain_json_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approve_chain_json", remarks: "工作流审批链行报文") {
            column(name: "CHAIN_LINE_ID", type: "bigint", remarks: "审批链行ID") { constraints(nullable: "false") }
            column(name: "CHAIN_ID", type: "bigint", remarks: "审批链头ID") { constraints(nullable: "false") }
            column(name: "CHAIN_WEB_JSON", type: "longtext", remarks: "审批链前端报文") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "CHAIN_LINE_ID", tableName: "hwkf_def_approve_chain_json", constraintName: "hwkf_def_approve_chain_json_u1")
    }
}