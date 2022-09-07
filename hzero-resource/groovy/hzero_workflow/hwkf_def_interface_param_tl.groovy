package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_interface_param_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_interface_param_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_interface_param_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_interface_param_tl", remarks: "工作流接口参数多语言") {
            column(name: "PARAMETER_ID", type: "bigint", remarks: "接口参数ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "PARAMETER_NAME", type: "varchar(" + 240 * weight + ")", remarks: "接口参数名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PARAMETER_ID,LANG", tableName: "hwkf_def_interface_param_tl", constraintName: "hwkf_def_interface_param_tl_u1")
    }
}