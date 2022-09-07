package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_ota_package.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_ota_package") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_ota_package_s', startValue: "1")
        }
        createTable(tableName: "hiot_ota_package", remarks: "ota基础信息表") {
            column(name: "PACKAGE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "THING_MODEL_ID", type: "bigint", remarks: "设备模板主键")
            column(name: "PROTOCOL_SERVICE", type: "varchar(" + 50 * weight + ")", remarks: "协议入口")
            column(name: "PACKAGE_NAME", type: "varchar(" + 64 * weight + ")", remarks: "升级包名称") { constraints(nullable: "false") }
            column(name: "CURRENT_FILE_NAME", type: "varchar(" + 255 * weight + ")", remarks: "文件名") { constraints(nullable: "false") }
            column(name: "CURRENT_VERSION", type: "varchar(" + 20 * weight + ")", remarks: "版本") { constraints(nullable: "false") }
            column(name: "CATEGORY_CODE", type: "varchar(" + 16 * weight + ")", remarks: "类别，区分设备和网关(HIOT.OTA_PACKAGE_CATEGORY)") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TENANT_ID,THING_MODEL_ID,CURRENT_VERSION", tableName: "hiot_ota_package", constraintName: "hiot_ota_package_u1")
        addUniqueConstraint(columnNames: "TENANT_ID,PACKAGE_NAME", tableName: "hiot_ota_package", constraintName: "hiot_ota_package_u2")
    }
}