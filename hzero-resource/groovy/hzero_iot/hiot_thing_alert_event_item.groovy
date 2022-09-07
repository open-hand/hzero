package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_alert_event_item.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_alert_event_item") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_alert_event_item_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_alert_event_item", remarks: "告警事件关联项表") {
            column(name: "EVENT_ITEM_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "ALERT_EVENT_ID", type: "bigint", remarks: "告警事件id,hiot_alert_event.alert_event_id") { constraints(nullable: "false") }
            column(name: "ITEM_NAME", type: "varchar(" + 30 * weight + ")", remarks: "数据点/监测指标唯一标识") { constraints(nullable: "false") }
            column(name: "VALUE", type: "varchar(" + 20 * weight + ")", remarks: "值")
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id,hpfm_tenant.tenant_id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "ALERT_EVENT_ID,ITEM_NAME,TENANT_ID", tableName: "hiot_thing_alert_event_item", constraintName: "hiot_thing_alert_event_item_u1")
    }
}