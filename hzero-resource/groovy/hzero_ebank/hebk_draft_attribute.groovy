package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_draft_attribute.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_draft_attribute") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_draft_attribute_s', startValue: "10001")
        }
        createTable(tableName: "hebk_draft_attribute", remarks: "票据属性") {
            column(name: "draft_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，hebk_draft.draft_id") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_draft_attribute_pk")
            }
            column(name: "attribute", type: "text", remarks: "属性") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-12_hebk_draft_attribute") {
        addColumn(tableName: 'hebk_draft_attribute') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID", afterColumn: 'attribute')
        }
    }
}
