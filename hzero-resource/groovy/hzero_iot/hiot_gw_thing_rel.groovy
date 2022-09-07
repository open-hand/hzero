package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gw_thing_rel.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gw_thing_rel") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_gw_thing_rel_s', startValue: "1")
        }
        createTable(tableName: "hiot_gw_thing_rel", remarks: "网关设备关系") {
            column(name: "REL_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "GATEWAY_ID", type: "bigint", remarks: "网关ID, hiot_gateway.GATEWAY_ID") { constraints(nullable: "false") }
            column(name: "THING_ID", type: "bigint", remarks: "设备ID, hiot_thing.THING_ID") { constraints(nullable: "false") }
            column(name: "TERMINAL_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "终端类型, 取自快码 HIOT.TERMINAL_TYPE") { constraints(nullable: "false") }
            column(name: "ENDPOINT_PROTOCOL", type: "varchar(" + 30 * weight + ")", remarks: "通信协议, 取自快码 HIOT.ENDPOINT_PROTOCOL") { constraints(nullable: "false") }
            column(name: "ENABLED", type: "tinyint", defaultValue: "1", remarks: "是否启用，0-否，1-是") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "GATEWAY_ID,THING_ID", tableName: "hiot_gw_thing_rel", constraintName: "hiot_gw_thing_rel_u1")
    }
}