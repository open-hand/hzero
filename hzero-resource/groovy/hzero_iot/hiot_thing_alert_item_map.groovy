package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_alert_item_map.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_alert_item_map") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_alert_item_map_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_alert_item_map", remarks: "设备告警关联项映射表") {
            column(name: "MAPPING_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "ALERT_REL_ID", type: "bigint", remarks: "设备告警关联表主键, hiot_thing_alert_rel.alert_rel_id") { constraints(nullable: "false") }
            column(name: "TARGET_KEY", type: "varchar(" + 60 * weight + ")", remarks: "预警平台告警配置, halt_alert_source_mapping.source_key") { constraints(nullable: "false") }
            column(name: "SOURCE_KEY", type: "varchar(" + 60 * weight + ")", remarks: "数据点/监测指标唯一标识") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id,hpfm_tenant.tenant_id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ALERT_REL_ID,TARGET_KEY,TENANT_ID", tableName: "hiot_thing_alert_item_map", constraintName: "hiot_thing_alert_item_map_u1")
    }
}