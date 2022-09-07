package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_condition_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_condition_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_condition_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_condition_tl", remarks: "工作流条件定义多语言表") {
            column(name: "CONDITION_ID", type: "bigint", remarks: "表ID，主键，供其他表做外键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "CONDITION_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批条件名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户Id") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "审批条件描述")
        }


        addUniqueConstraint(columnNames: "CONDITION_ID,LANG", tableName: "hwkf_def_condition_tl", constraintName: "hwkf_def_condition_tl_u1")
    }
}