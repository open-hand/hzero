package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_notice_node_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_notice_node_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_notice_node_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_notice_node_tl", remarks: "工作流通知节点多语言") {
            column(name: "NODE_ID", type: "bigint", remarks: "通知节点ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "NODE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "通知节点名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "NODE_ID,LANG", tableName: "hwkf_notice_node_tl", constraintName: "hwkf_notice_node_tl_u1")
    }
}