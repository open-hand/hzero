package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gw_edgink_parser.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gw_edgink_parser") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_gw_edgink_parser_s', startValue: "1")
        }
        createTable(tableName: "hiot_gw_edgink_parser", remarks: "Edgink子设备解析配置") {
            column(name: "PARSER_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "EDGINK_ID", type: "bigint", remarks: "modbus子设备ID, hiot_gateway_modbus.MODBUS_ID") { constraints(nullable: "false") }
            column(name: "PROPERTY_ID", type: "bigint", remarks: "数据点id") { constraints(nullable: "false") }
            column(name: "DC_DEVICE_TAG_ID", type: "bigint", remarks: "EDGINK采集设备项ID,hiot_egk_dc_device_tag.DC_DEVICE_TAG_ID") { constraints(nullable: "false") }
            column(name: "REQUEST_INTERVAL", type: "int", remarks: "请求间隔, 单位: 秒")
            column(name: "EDGINK_ITEM", type: "varchar(" + 45 * weight + ")", remarks: "Edgink对应采集项") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "EDGINK_ID,PROPERTY_ID", tableName: "hiot_gw_edgink_parser", constraintName: "hiot_gw_edgink_parser_u1")
        addUniqueConstraint(columnNames: "DC_DEVICE_TAG_ID", tableName: "hiot_gw_edgink_parser", constraintName: "hiot_gw_edgink_parser_u2")
    }
}