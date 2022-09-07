package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gw_modbus.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gw_modbus") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_gw_modbus_s', startValue: "1")
        }
        createTable(tableName: "hiot_gw_modbus", remarks: "Modbus子设备") {
            column(name: "MODBUS_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "REL_ID", type: "bigint", remarks: "网关和设备关系ID, hiot_gateway_thing_rel.REL_ID") { constraints(nullable: "false") }
            column(name: "CONNECTION_MODE", type: "varchar(" + 30 * weight + ")", remarks: "连接模式, 取自快码 HIOT.CONNECTION_MODE") { constraints(nullable: "false") }
            column(name: "SLAVE_STATION", type: "int", remarks: "Modbus从站编号") { constraints(nullable: "false") }
            column(name: "TCP_HOST", type: "varchar(" + 15 * weight + ")", remarks: "IP地址, 用于TCP")
            column(name: "TCP_PORT", type: "int", remarks: "端口号, 用于TCP")
            column(name: "RTU_SERIAL_PORT", type: "varchar(" + 64 * weight + ")", remarks: "串口号, 用于RTU")
            column(name: "RTU_BAUD_RATE", type: "int", remarks: "波特率, 用于RTU")
            column(name: "RTU_DATA_BIT", type: "int", remarks: "数据位, 取值7或8, 用于RTU")
            column(name: "RTU_CALIBRATION_METHOD", type: "varchar(" + 30 * weight + ")", remarks: "取值校验, 取自快码 HIOT.CALIBRATION_METHOD, 用于RTU")
            column(name: "RTU_STOP_BIT", type: "int", remarks: "停止位, 取值1或2, 用于RTU")
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}