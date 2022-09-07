package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_task_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_task_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_task_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_task_tl", remarks: "") {
            column(name: "TASK_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "TASK_NAME", type: "varchar(" + 100 * weight + ")", remarks: "节点名称")
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TASK_ID,LANG", tableName: "hwkf_run_task_tl", constraintName: "hwkf_run_task_tl_ul")
    }
}