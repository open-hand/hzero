package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_label_template_tl.groovy') {
    changeSet(author: "hzero", id: "2020-09-04-hrpt_label_template_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hrpt_label_template_tl_s', startValue: "1")
        }
        createTable(tableName: "hrpt_label_template_tl", remarks: "标签模板多语言") {
            column(name: "label_template_id", type: "bigint", remarks: "标签模板Id") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "template_name", type: "varchar(" + 60 * weight + ")", remarks: "模板名称") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "label_template_id,lang", tableName: "hrpt_label_template_tl", constraintName: "hrpt_label_template_tl_u1")
    }
}