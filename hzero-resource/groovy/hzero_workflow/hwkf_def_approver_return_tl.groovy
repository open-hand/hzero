package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_return_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approver_return_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approver_return_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approver_return_tl", remarks: "工作流审批人规则返回值表") {
            column(name: "RETURN_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "FIELD_NAME", type: "varchar(" + 80 * weight + ")", remarks: "字段名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "")
        }


        addUniqueConstraint(columnNames: "RETURN_ID,LANG", tableName: "hwkf_def_approver_return_tl", constraintName: "hwkf_def_approver_return_tl_u1")
    }
}