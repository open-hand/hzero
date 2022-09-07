package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_form_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_form_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_form_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_form_tl", remarks: "工作流审批表单多语言") {
            column(name: "FORM_ID", type: "bigint", remarks: "审批表单ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "FORM_DESCRIPTION", type: "varchar(" + 80 * weight + ")", remarks: "审批表单描述") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "FORM_ID,LANG", tableName: "hwkf_def_form_tl", constraintName: "hwkf_def_form_tl_u1")
    }
}