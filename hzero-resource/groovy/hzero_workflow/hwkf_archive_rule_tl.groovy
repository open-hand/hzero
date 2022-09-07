package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_archive_rule_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_archive_rule_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_archive_rule_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_archive_rule_tl", remarks: "流程归档规则多语言表") {
            column(name: "ARCH_ID", type: "bigint", remarks: "归档规则ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "ARCH_NAME", type: "varchar(" + 80 * weight + ")", remarks: "归档规则名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ARCH_ID,LANG", tableName: "hwkf_archive_rule_tl", constraintName: "hwkf_archive_rule_tl_u1")
    }
}