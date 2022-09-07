package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_group.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_group") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_group_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_group", remarks: "设备分组信息") {
            column(name: "THING_GROUP_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "PARENT_ID", type: "bigint", defaultValue: "-1", remarks: "父级ID,没有父级为-1") { constraints(nullable: "false") }
            column(name: "CODE", type: "varchar(" + 60 * weight + ")", remarks: "设备分组编码") { constraints(nullable: "false") }
            column(name: "NAME", type: "varchar(" + 240 * weight + ")", remarks: "设备分组名称") { constraints(nullable: "false") }
            column(name: "LEVEL_PATH", type: "varchar(" + 255 * weight + ")", remarks: "继承关系的层级路径") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用。1启用，0未启用") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hiot_thing_group", indexName: "hiot_thing_group_n1") {
            column(name: "LEVEL_PATH")
        }

        addUniqueConstraint(columnNames: "TENANT_ID,CODE", tableName: "hiot_thing_group", constraintName: "hiot_thing_group_u1")
    }
}