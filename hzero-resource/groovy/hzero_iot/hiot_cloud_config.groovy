package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_cloud_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_cloud_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_cloud_config_s', startValue: "1")
        }
        createTable(tableName: "hiot_cloud_config", remarks: "云配置信息") {
            column(name: "CONFIG_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "CONFIG_CODE", type: "varchar(" + 255 * weight + ")", remarks: "配置文件编码")
            column(name: "CONFIG_NAME", type: "varchar(" + 20 * weight + ")", remarks: "配置文件名称，识别配置唯一标志") { constraints(nullable: "false") }
            column(name: "PLATFORM", type: "varchar(" + 20 * weight + ")", remarks: "取自快码，HIOT.CLOUD_PLATFORM") { constraints(nullable: "false") }
            column(name: "ACCESS_KEY", type: "varchar(" + 128 * weight + ")", remarks: "云平台Access key， AES128加密后的密文") { constraints(nullable: "false") }
            column(name: "SECRET_KEY", type: "varchar(" + 128 * weight + ")", remarks: "云平台Secret key， AES128加密后的密文") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "ADDITION", type: "text", remarks: "不同云平台的差异信息")
            column(name: "ENABLED_FLAG", type: "tinyint", remarks: "是否启用。1启用，0未启用") { constraints(nullable: "false") }
            column(name: "SERVER_CODE", type: "varchar(" + 32 * weight + ")", remarks: "接口平台服务编码")
            column(name: "ENDPOINT", type: "varchar(" + 100 * weight + ")", remarks: "IOT API访问域名,如iotdm.gz.baidubce.com")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "CONFIG_CODE,TENANT_ID", tableName: "hiot_cloud_config", constraintName: "hiot_cloud_config_u2")
        addUniqueConstraint(columnNames: "CONFIG_NAME,TENANT_ID", tableName: "hiot_cloud_config", constraintName: "hiot_cloud_config_u1")
    }
}