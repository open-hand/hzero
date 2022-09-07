package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approve_chain_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approve_chain_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approve_chain_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approve_chain_tl", remarks: "工作流审批链头") {
            column(name: "CHAIN_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "CHAIN_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批链名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "")
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "")
        }


        addUniqueConstraint(columnNames: "CHAIN_ID,LANG", tableName: "hwkf_def_approve_chain_tl", constraintName: "hwkf_def_approve_chain_tl_u1")
    }
}