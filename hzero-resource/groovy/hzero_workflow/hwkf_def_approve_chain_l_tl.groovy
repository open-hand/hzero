package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approve_chain_l_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approve_chain_l_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approve_chain_l_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approve_chain_l_tl", remarks: "工作流审批链头") {
            column(name: "CHAIN_LINE_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "审批链行描述") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "CHAIN_LINE_ID,LANG", tableName: "hwkf_def_approve_chain_l_tl", constraintName: "hwkf_def_approve_chain_l_tl_u1")
    }
}