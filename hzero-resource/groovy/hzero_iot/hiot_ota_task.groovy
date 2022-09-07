package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_ota_task.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_ota_task") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_ota_task_s', startValue: "1")
        }
        createTable(tableName: "hiot_ota_task", remarks: "OTA升级任务") {
            column(name: "TASK_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "CONFIG_ID", type: "bigint", remarks: "云账户配置主键") { constraints(nullable: "false") }
            column(name: "TASK_NAME", type: "varchar(" + 64 * weight + ")", remarks: "任务名称") { constraints(nullable: "false") }
            column(name: "TASK_CODE", type: "varchar(" + 64 * weight + ")", remarks: "任务编码") { constraints(nullable: "false") }
            column(name: "PACKAGE_ID", type: "bigint", remarks: "升级包主键") { constraints(nullable: "false") }
            column(name: "MSG_TEMPLATE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "报文模板编码，hiot_msg_template.template_code") { constraints(nullable: "false") }
            column(name: "SCHEDULED_START_TIME", type: "datetime", remarks: "升级开始时间，默认为当前时间")
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "THING_RANGE", type: "tinyint", defaultValue: "1", remarks: "设备筛选范围。1全量，0特定范围")
            column(name: "REAL_START_TIME", type: "datetime", remarks: "实际开始时间")
            column(name: "REAL_END_TIME", type: "datetime", remarks: "实际结束时间")
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TENANT_ID,TASK_CODE", tableName: "hiot_ota_task", constraintName: "hiot_ota_task_u1")
    }
}