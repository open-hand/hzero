package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_property_model.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_property_model") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_property_model_s', startValue: "1")
        }
        createTable(tableName: "hiot_property_model", remarks: "数据点模型") {
            column(name: "PROPERTY_MODEL_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "数据点类型ID, hiot_property_type.type_id") { constraints(nullable: "false") }
            column(name: "PROPERTY_MODEL_CODE", type: "varchar(" + 30 * weight + ")", remarks: "编码") { constraints(nullable: "false") }
            column(name: "PROPERTY_MODEL_NAME", type: "varchar(" + 45 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "UNIT_CODE", type: "varchar(" + 30 * weight + ")", remarks: "计量单位code, hpfm_unit.unit_code, 用于测量点, 数值型控制参数")
            column(name: "REPORT_INTERVAL", type: "bigint", remarks: "采集间隔, 单位:秒, 用于测量点,状态点")
            column(name: "MIN_VALUE", type: "varchar(" + 11 * weight + ")", remarks: "合理值范围最小值, 用于测量点, 数值型控制参数")
            column(name: "MAX_VALUE", type: "varchar(" + 11 * weight + ")", remarks: "合理值范围最大值, 用于测量点, 数值型控制参数")
            column(name: "VALUE_PRECISION", type: "int", remarks: "取值精度, 用于测量点, 数值型控制参数")
            column(name: "OPTIONS", type: "varchar(" + 1200 * weight + ")", remarks: "可选项(json串), 用于状态点, 布尔和枚举型控制参数")
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "PROPERTY_MODEL_CODE,TENANT_ID", tableName: "hiot_property_model", constraintName: "hiot_property_model_u1")
        addUniqueConstraint(columnNames: "PROPERTY_MODEL_NAME,TENANT_ID", tableName: "hiot_property_model", constraintName: "hiot_property_model_u2")
    }
}