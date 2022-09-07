package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_egk_dc_device_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_egk_dc_device_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        createTable(tableName: "hiot_egk_dc_device_tl", remarks: "设备维护多语言表") {
            column(name: "DC_DEVICE_ID", type: "bigint", remarks: "表ID，主键") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 255 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "DC_DEVICE_ID,LANG", tableName: "hiot_egk_dc_device_tl", constraintName: "edgink_dc_device_tl_u1")
    }
}