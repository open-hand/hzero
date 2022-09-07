package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_type_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_type_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_type_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_type_tl", remarks: "工作流类型多语言表") {
            column(name: "TYPE_ID", type: "bigint", remarks: "类型ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "TYPE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "工作流类型名称") { constraints(nullable: "false") }
            column(name: "TYPE_DESC", type: "varchar(" + 255 * weight + ")", remarks: "工作流类型描述")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户Id") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TYPE_ID,LANG", tableName: "hwkf_def_type_tl", constraintName: "hwkf_def_type_tl_u1")
    }
}