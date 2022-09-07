package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_prop_value_history.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_prop_value_history") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_prop_value_history_s', startValue: "1")
        }
        createTable(tableName: "hiot_prop_value_history", remarks: "设备数据点上报历史记录") {
            column(name: "HISTORY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "THING_ID", type: "bigint", remarks: "设备ID, hiot_thing.THING_ID") { constraints(nullable: "false") }
            column(name: "PROPERTY_ID", type: "bigint", remarks: "数据点ID, hiot_property.PROPERTY_ID") { constraints(nullable: "false") }
            column(name: "PROPERTY_GUID", type: "varchar(" + 30 * weight + ")", remarks: "数据点唯一标识, hiot_property.GUID") { constraints(nullable: "false") }
            column(name: "VAL", type: "varchar(" + 30 * weight + ")", remarks: "值") { constraints(nullable: "false") }
            column(name: "TS", type: "datetime", remarks: "时间") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PROPERTY_GUID,TS", tableName: "hiot_prop_value_history", constraintName: "hiot_prop_value_history_u2")
        addUniqueConstraint(columnNames: "PROPERTY_ID,TS", tableName: "hiot_prop_value_history", constraintName: "hiot_prop_value_history_u1")
    }
}