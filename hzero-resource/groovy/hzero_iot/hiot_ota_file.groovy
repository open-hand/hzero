package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_ota_file.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_ota_file") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_ota_file_s', startValue: "1")
        }
        createTable(tableName: "hiot_ota_file", remarks: "ota文件信息表") {
            column(name: "OTA_FILE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "PACKAGE_ID", type: "bigint", remarks: "OTA升级包id") { constraints(nullable: "false") }
            column(name: "VERSION_NUM", type: "varchar(" + 20 * weight + ")", remarks: "当前的版本号") { constraints(nullable: "false") }
            column(name: "ATTACHMENT_URL", type: "varchar(" + 255 * weight + ")", remarks: "附件存储URL")
            column(name: "FILE_NAME", type: "varchar(" + 255 * weight + ")", remarks: "文件名称")
            column(name: "FILE_SIZE", type: "varchar(" + 22 * weight + ")", defaultValue: "1", remarks: "固件包文件大小， 单位为 MB")
            column(name: "SIGN_METHOD", type: "varchar(" + 20 * weight + ")", remarks: "签名算法，取自值集[HIOT.OTA_SIGN_METHOD]")
            column(name: "SIGN_VALUE", type: "varchar(" + 512 * weight + ")", remarks: "签名算法后的值")
            column(name: "UPDATE_LOGS", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PACKAGE_ID,VERSION_NUM", tableName: "hiot_ota_file", constraintName: "hiot_oat_file_u1")
    }
}