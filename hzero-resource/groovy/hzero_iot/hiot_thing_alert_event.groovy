package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_alert_event.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_alert_event") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_alert_event_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_alert_event", remarks: "告警事件记录表") {
            column(name: "ALERT_EVENT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "THING_ID", type: "bigint", remarks: "设备表主键id,hiot_thing.THING_ID") { constraints(nullable: "false") }
            column(name: "ALERT_CODE", type: "varchar(" + 60 * weight + ")", remarks: "预警平台告警配置编码,halt_alert.alert_code") { constraints(nullable: "false") }
            column(name: "EVENT_TIME", type: "datetime", remarks: "事件产生时间") { constraints(nullable: "false") }
            column(name: "ALERT_LEVEL", type: "varchar(" + 20 * weight + ")", remarks: "告警级别")
            column(name: "ALERT_RANGE_CODE", type: "varchar(" + 20 * weight + ")", remarks: "预警等级")
            column(name: "RECOVERED_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否恢复, 1表示恢复，0表示未恢复") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id,hpfm_tenant.tenant_id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "THING_ID,ALERT_CODE,EVENT_TIME,TENANT_ID", tableName: "hiot_thing_alert_event", constraintName: "hiot_thing_alert_event_u1")
    }
}