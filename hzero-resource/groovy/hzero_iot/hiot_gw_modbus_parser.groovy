package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gw_modbus_parser.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gw_modbus_parser") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_gw_modbus_parser_s', startValue: "1")
        }
        createTable(tableName: "hiot_gw_modbus_parser", remarks: "Modbus子设备解析配置") {
            column(name: "PARSER_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "MODBUS_ID", type: "bigint", remarks: "modbus子设备ID, hiot_gateway_modbus.MODBUS_ID") { constraints(nullable: "false") }
            column(name: "PROPERTY_ID", type: "bigint", remarks: "数据点ID, hiot_property.PROPERTY_ID") { constraints(nullable: "false") }
            column(name: "BIT_SEGMENT", type: "int", remarks: "保持寄存器数据段")
            column(name: "RH", type: "varchar(" + 30 * weight + ")", remarks: "量程上限")
            column(name: "RL", type: "varchar(" + 30 * weight + ")", remarks: "量程下限")
            column(name: "DATA_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "数据类型, 取自快码 HIOT.PARSER_DATA_TYPE") { constraints(nullable: "false") }
            column(name: "DATA_LENGTH", type: "int", remarks: "数据长度") { constraints(nullable: "false") }
            column(name: "OPERATION_CODE", type: "int", remarks: "操作码") { constraints(nullable: "false") }
            column(name: "REGISTER_START_ADDRESS", type: "int", remarks: "寄存器开始地址") { constraints(nullable: "false") }
            column(name: "READ_LENGTH", type: "int", remarks: "读取长度") { constraints(nullable: "false") }
            column(name: "REQUEST_INTERVAL", type: "int", remarks: "请求间隔, 单位: 秒")
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "MODBUS_ID,PROPERTY_ID", tableName: "hiot_gw_modbus_parser", constraintName: "hiot_gw_modbus_parser_u1")
    }
}