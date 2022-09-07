package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_alert_rel.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_alert_rel") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_thing_alert_rel_s', startValue: "1")
        }
        createTable(tableName: "hiot_thing_alert_rel", remarks: "设备告警关联表") {
            column(name: "ALERT_REL_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "THING_ID", type: "bigint", remarks: "设备表主键id,hiot_thing.THING_ID") { constraints(nullable: "false") }
            column(name: "ALERT_CODE", type: "varchar(" + 60 * weight + ")", remarks: "预警平台告警配置编码,halt_alert.alert_code") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id,hpfm_tenant.tenant_id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "THING_ID,ALERT_CODE,TENANT_ID", tableName: "hiot_thing_alert_rel", constraintName: "hiot_thing_alert_rel_u1")
    }
}