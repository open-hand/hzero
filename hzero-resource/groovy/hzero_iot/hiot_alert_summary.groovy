package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_alert_summary.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_alert_summary") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_alert_summary_s', startValue: "1")
        }
        createTable(tableName: "hiot_alert_summary", remarks: "告警事件统计表") {
            column(name: "SUMMARY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "THING_GROUP_ID", type: "bigint", remarks: "设备组id")
            column(name: "SUMMARY_DATE", type: "datetime", remarks: "统计时间") { constraints(nullable: "false") }
            column(name: "ALERT_LEVEL", type: "varchar(" + 30 * weight + ")", remarks: "告警级别, 取自快码 HIOT.ALERT_LEVEL") { constraints(nullable: "false") }
            column(name: "TOTAL_COUNT", type: "bigint", remarks: "告警总数") { constraints(nullable: "false") }
            column(name: "RECOVERED_COUNT", type: "bigint", remarks: "实时未修复告警数量") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TENANT_ID,THING_GROUP_ID,SUMMARY_DATE,ALERT_LEVEL", tableName: "hiot_alert_summary", constraintName: "hiot_gw_alert_summary_u1")
    }
}