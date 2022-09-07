package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_workflow_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_workflow_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_workflow_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_workflow_tl", remarks: "工作流流程定义多语言表") {
            column(name: "FLOW_ID", type: "bigint", remarks: "流程定义ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "FLOW_NAME", type: "varchar(" + 240 * weight + ")", remarks: "流程名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "FLOW_ID,LANG", tableName: "hwkf_def_workflow_tl", constraintName: "hwkf_def_workflow_tl_u1")
    }
}