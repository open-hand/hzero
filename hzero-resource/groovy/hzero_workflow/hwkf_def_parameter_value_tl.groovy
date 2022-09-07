package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_parameter_value_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_parameter_value_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_parameter_value_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_parameter_value_tl", remarks: "工作流流程变量关联参数多语言表") {
            column(name: "PARAMETER_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "PARAMETER_NAME", type: "varchar(" + 80 * weight + ")", remarks: "参数名称")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户Id") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PARAMETER_ID,LANG", tableName: "hwkf_def_parameter_value_tl", constraintName: "hwkf_def_parameter_value_tl_u1")
    }
}