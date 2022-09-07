package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_egk_dc_device.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_egk_dc_device") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_egk_dc_device_s', startValue: "1")
        }
        createTable(tableName: "hiot_egk_dc_device", remarks: "设备维护表") {
            column(name: "DC_DEVICE_ID", type: "bigint", autoIncrement: true, remarks: "主键ID") { constraints(primaryKey: true) }
            column(name: "GATEWAY_ID", type: "bigint", defaultValue: "1", remarks: "hiot_gateway.GATEWAY_ID") { constraints(nullable: "false") }
            column(name: "DC_DEVICE_CODE", type: "varchar(" + 60 * weight + ")", remarks: "数据采集设备编码") { constraints(nullable: "false") }
            column(name: "HEARTBEAT_CYCLE", type: "bigint", defaultValue: "100000", remarks: "设备连接重连周期") { constraints(nullable: "false") }
            column(name: "PACKAGE_ID", type: "bigint", defaultValue: "1", remarks: "固件包ID，hiot_ota_package.package_id") { constraints(nullable: "false") }
            column(name: "CONNECT_INFO", type: "varchar(" + 255 * weight + ")", remarks: "设备连接属性") { constraints(nullable: "false") }
            column(name: "SIMULATOR_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否模拟设备标记，1-模拟，0-真实设备，待议") { constraints(nullable: "false") }
            column(name: "ENABLE_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用标记，1-启用，0-禁用") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "GATEWAY_ID,DC_DEVICE_CODE,TENANT_ID", tableName: "hiot_egk_dc_device", constraintName: "EDGINK_DC_DEVICE_U1")
    }
}