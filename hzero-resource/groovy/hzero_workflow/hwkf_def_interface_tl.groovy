package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_interface_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_interface_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_interface_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_interface_tl", remarks: "工作流接口定义多语言") {
            column(name: "INTERFACE_ID", type: "bigint", remarks: "接口ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "INTERFACE_NAME", type: "varchar(" + 240 * weight + ")", remarks: "接口名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "INTERFACE_ID,LANG", tableName: "hwkf_def_interface_tl", constraintName: "hwkf_def_interface_tl_u1")
    }
}