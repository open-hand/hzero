package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_egk_dc_device_tag.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_egk_dc_device_tag") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_egk_dc_device_tag_s', startValue: "1")
        }
        createTable(tableName: "hiot_egk_dc_device_tag", remarks: "设备维护地址表") {
            column(name: "DC_DEVICE_TAG_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "DC_DEVICE_ID", type: "bigint", defaultValue: "1", remarks: "hiot_egk_dc_device表的ID") { constraints(nullable: "false") }
            column(name: "PARAMETER", type: "varchar(" + 50 * weight + ")", remarks: "参数") { constraints(nullable: "false") }
            column(name: "ORDER_CODE", type: "varchar(" + 50 * weight + ")", remarks: "排序编码") { constraints(nullable: "false") }
            column(name: "ADDRESS", type: "varchar(" + 50 * weight + ")", remarks: "地址") { constraints(nullable: "false") }
            column(name: "DATA_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "数据类类型") { constraints(nullable: "false") }
            column(name: "CLIENT_ACCESS", type: "varchar(" + 30 * weight + ")", remarks: "读写权限") { constraints(nullable: "false") }
            column(name: "FREQUENCY", type: "bigint", defaultValue: "1000", remarks: "频率") { constraints(nullable: "false") }
            column(name: "MULTIPLE", type: "bigint", defaultValue: "1", remarks: "缩放倍数") { constraints(nullable: "false") }
            column(name: "RECORD_CHANGES_FLAG", type: "tinyint", defaultValue: "1", remarks: "记录变化") { constraints(nullable: "false") }
            column(name: "PUBLISHED_FLAG", type: "tinyint", defaultValue: "1", remarks: "推送标识") { constraints(nullable: "false") }
            column(name: "TRIGGER_FLAG", type: "tinyint", defaultValue: "1", remarks: "轮巡式采集") { constraints(nullable: "false") }
            column(name: "ENABLE_FLAG", type: "tinyint", defaultValue: "1", remarks: "1-启用，0-禁用") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "描述") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "DC_DEVICE_ID,PARAMETER", tableName: "hiot_egk_dc_device_tag", constraintName: "hiot_egk_dc_device_tag_u1")
        addUniqueConstraint(columnNames: "DC_DEVICE_ID,ADDRESS", tableName: "hiot_egk_dc_device_tag", constraintName: "hiot_egk_dc_device_tag_u2")
    }
}