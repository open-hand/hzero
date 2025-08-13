package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_common_template.groovy') {
    changeSet(author: "hzero", id: "2020-09-04-hpfm_common_template") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_common_template_s', startValue: "1")
        }
        createTable(tableName: "hpfm_common_template", remarks: "通用模板") {
            column(name: "template_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "template_code", type: "varchar(" + 60 * weight + ")", remarks: "模板编码") { constraints(nullable: "false") }
            column(name: "template_name", type: "varchar(" + 240 * weight + ")", remarks: "模板名称") { constraints(nullable: "false") }
            column(name: "template_category_code", type: "varchar(" + 150 * weight + ")", remarks: "模板分类.HPFM.TEMPLATE_CATEGORY") { constraints(nullable: "false") }
            column(name: "template_content", type: "longtext", remarks: "模板内容") { constraints(nullable: "false") }
            column(name: "lang", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用。1启用，0未启用") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "template_code,lang,tenant_id", tableName: "hpfm_common_template", constraintName: "hpfm_common_template_u1")
    }
}