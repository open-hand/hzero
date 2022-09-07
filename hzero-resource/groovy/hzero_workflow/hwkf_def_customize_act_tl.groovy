package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_customize_act_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_customize_act_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_customize_act_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_customize_act_tl", remarks: "工作流自定义审批动作多语言") {
            column(name: "ACTION_ID", type: "bigint", remarks: "动作ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "ACTION_NAME", type: "varchar(" + 80 * weight + ")", remarks: "动作名称") { constraints(nullable: "false") }
            column(name: "ACTION_DESC", type: "varchar(" + 240 * weight + ")", remarks: "动作描述")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ACTION_ID,LANG", tableName: "hwkf_def_customize_act_tl", constraintName: "hwkf_def_customize_act_tl_u1")
    }
}