package script.db

databaseChangeLog(logicalFilePath: 'script/db/hnlp_template_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-hnlp_template_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: "hnlp_template_tl_s", startValue: "1")
        }
        createTable(tableName: "hnlp_template_tl", remarks: "") {
            column(name: "TEMPLATE_ID", type: "bigint", remarks: "hnlp_template表ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 16 * weight + ")", remarks: "语言编码", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "TEMPLATE_NAME", type: "varchar(" + 100 * weight + ")", remarks: "模板名称", defaultValue: "") {
                constraints(nullable: "false")
            }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-09-02-1-hnlp_template_tl") {
        addUniqueConstraint(columnNames: "TEMPLATE_ID,LANG", tableName: "hnlp_template_tl", constraintName: "hnlp_template_tl_u1")
    }
}
