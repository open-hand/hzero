package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_msg_template_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_msg_template_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        createTable(tableName: "hiot_msg_template_tl", remarks: "报文模板配置多语言表") {
            column(name: "template_id", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 20 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "template_name", type: "varchar(" + 120 * weight + ")", remarks: "报文模板名称") { constraints(nullable: "false") }
            column(name: "description", type: "varchar(" + 240 * weight + ")", remarks: "说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户id,hpfm_tenant.tenant_id") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "template_id,lang", tableName: "hiot_msg_template_tl", constraintName: "hiot_msg_template_tl_u1")
    }
}