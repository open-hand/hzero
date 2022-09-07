package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_mail_template_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_mail_template_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_mail_template_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_mail_template_tl", remarks: "工作流邮件审批多语言") {
            column(name: "TEMPLATE_ID", type: "bigint", remarks: "邮件模板ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "TEMPLATE_NAME", type: "varchar(" + 240 * weight + ")", remarks: "邮件模板名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TEMPLATE_ID,LANG", tableName: "hwkf_def_mail_template_tl", constraintName: "TEMPLATE_ID")
    }
}