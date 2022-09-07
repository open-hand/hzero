package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_msg_template.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_msg_template") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_msg_template_s', startValue: "1")
        }
        createTable(tableName: "hiot_msg_template", remarks: "报文模板配置表") {
            column(name: "template_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "template_code", type: "varchar(" + 30 * weight + ")", remarks: "报文模板编码") { constraints(nullable: "false") }
            column(name: "template_name", type: "varchar(" + 120 * weight + ")", remarks: "报文模板名称") { constraints(nullable: "false") }
            column(name: "template_type_code", type: "varchar(" + 30 * weight + ")", remarks: "报文模板类型，值集HIOT.MSG_TEMPLATE_TYPE") { constraints(nullable: "false") }
            column(name: "msg_content", type: "longtext", remarks: "报文模板内容")
            column(name: "description", type: "varchar(" + 240 * weight + ")", remarks: "说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户id，hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "template_code,tenant_id", tableName: "hiot_msg_template", constraintName: "hiot_msg_template_u1")
    }
}