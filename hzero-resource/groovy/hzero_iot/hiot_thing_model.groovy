package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_model.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_model") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_model_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_model", remarks: "设备模型") {
            column(name: "THING_MODEL_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "CONFIG_ID", type: "bigint", remarks: "云账号配置ID") { constraints(nullable: "false") }
            column(name: "cloud_model_name", type: "varchar(" + 60 * weight + ")", remarks: "云端模型名称")
            column(name: "CONSUMER_GROUP_ID", type: "bigint", remarks: "消费者组ID")
            column(name: "THING_MODEL_CODE", type: "varchar(" + 30 * weight + ")", remarks: "编码") { constraints(nullable: "false") }
            column(name: "THING_MODEL_NAME", type: "varchar(" + 45 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "CATEGORY", type: "varchar(" + 30 * weight + ")", remarks: "设备类别, 取自快码 HIOT.THING_CATEGORY")
            column(name: "STATUS", type: "varchar(" + 30 * weight + ")", remarks: "设备状态")
            column(name: "ENABLED", type: "tinyint", defaultValue: "1", remarks: "启用, 1-启用, 0-禁用") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "THING_MODEL_CODE,TENANT_ID", tableName: "hiot_thing_model", constraintName: "hiot_thing_model_u2")
        addUniqueConstraint(columnNames: "cloud_model_name,CONFIG_ID", tableName: "hiot_thing_model", constraintName: "hiot_thing_model_u1")
    }
}