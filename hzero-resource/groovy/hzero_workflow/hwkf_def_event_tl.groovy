package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_event_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_event_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_event_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_event_tl", remarks: "工作流事件多语言") {
            column(name: "EVENT_ID", type: "bigint", remarks: "审批事件ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "EVENT_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批事件名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "EVENT_ID,LANG", tableName: "hwkf_def_event_tl", constraintName: "hwkf_def_event_tl_u1")
    }
}