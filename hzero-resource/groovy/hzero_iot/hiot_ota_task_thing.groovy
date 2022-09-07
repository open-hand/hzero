package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_ota_task_thing.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_ota_task_thing") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_ota_task_thing_s', startValue: "1")
        }
        createTable(tableName: "hiot_ota_task_thing", remarks: "OTA升级任务关联设备") {
            column(name: "TASK_THING_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "TASK_ID", type: "bigint", remarks: "升级任务主键") { constraints(nullable: "false") }
            column(name: "THING_ID", type: "bigint", remarks: "设备主键ID") { constraints(nullable: "false") }
            column(name: "VERSION_BEFORE", type: "varchar(" + 20 * weight + ")", remarks: "升级前版本")
            column(name: "START_TIME", type: "datetime", remarks: "开始时间")
            column(name: "END_TIME", type: "datetime", remarks: "结束时间")
            column(name: "OTA_STATUS", type: "varchar(" + 30 * weight + ")", remarks: "当前升级状态， 取自值集[HIOT.OTA_STATUS]")
            column(name: "ERROR_CODE", type: "varchar(" + 10 * weight + ")", remarks: "错误代码")
            column(name: "ERROR_MESSAGE", type: "varchar(" + 255 * weight + ")", remarks: "错误消息文本")
            column(name: "RETRY_TIME", type: "datetime", remarks: "重试时间")
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TENANT_ID,TASK_ID,THING_ID", tableName: "hiot_ota_task_thing", constraintName: "hiot_ota_task_thing_u1")
    }
}