package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_property.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_property") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_property_s', startValue: "1")
        }
        createTable(tableName: "hiot_property", remarks: "数据点") {
            column(name: "PROPERTY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "GUID", type: "varchar(" + 30 * weight + ")", remarks: "唯一标识, p-随机码") { constraints(nullable: "false") }
            column(name: "THING_ID", type: "bigint", remarks: "数据点所属设备ID, hiot_thing.THING_ID") { constraints(nullable: "false") }
            column(name: "PROPERTY_CODE", type: "varchar(" + 200 * weight + ")", remarks: "编码") { constraints(nullable: "false") }
            column(name: "PROPERTY_NAME", type: "varchar(" + 240 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "ITEM_ID", type: "bigint", remarks: "数据点模板ID(hiot_property_model.property_model_id)或采集项ID(hiot_egk_dc_device_tag.TAG_ID)") { constraints(nullable: "false") }
            column(name: "ITEM_TYPE", type: "bigint", defaultValue: "0", remarks: "0: 数据点模板  1: 采集项") { constraints(nullable: "false") }
            column(name: "UNIT_CODE", type: "varchar(" + 30 * weight + ")", remarks: "计量单位code, hpfm_unit.unit_code, 用于测量点, 数值型控制参数")
            column(name: "REPORT_INTERVAL", type: "bigint", remarks: "采集间隔, 单位:秒, 用于测量点,状态点")
            column(name: "CATEGORY", type: "varchar(" + 30 * weight + ")", remarks: "分类, 取自快码 HIOT.PROPERTY_TYPE_CATEGOR")
            column(name: "DATA_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "数据类型, 取自快码 HIOT.DATA_TYPE")
            column(name: "MIN_VALUE", type: "varchar(" + 11 * weight + ")", remarks: "合理值范围最小值, 用于测量点, 数值型控制参数")
            column(name: "MAX_VALUE", type: "varchar(" + 11 * weight + ")", remarks: "合理值范围最大值, 用于测量点, 数值型控制参数")
            column(name: "VALUE_PRECISION", type: "int", remarks: "取值精度, 用于测量点, 数值型控制参数")
            column(name: "OPTIONS", type: "varchar(" + 1200 * weight + ")", remarks: "可选项(json串), 用于状态点, 布尔和枚举型控制参数")
            column(name: "ALERT_MODEL_ID", type: "bigint", remarks: "告警模板ID, 用于测量点, 状态点, hiot_alert_model.ALERT_MODEL_ID")
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "GUID", tableName: "hiot_property", constraintName: "hiot_property_u1")
    }
}