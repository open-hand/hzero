package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_log_msg_down.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_log_msg_down") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_log_msg_down_s', startValue: "1")
        }
        createTable(tableName: "hiot_log_msg_down", remarks: "数据下行日志表") {
            column(name: "DOWN_LOG_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "GUID", type: "varchar(" + 20 * weight + ")", remarks: "设备或网关的GUID")
            column(name: "SERVICE_INST_IP", type: "varchar(" + 20 * weight + ")", remarks: "服务实例IP") { constraints(nullable: "false") }
            column(name: "PLATFORM", type: "varchar(" + 20 * weight + ")", remarks: "云平台")
            column(name: "TOPIC_NAME", type: "varchar(" + 255 * weight + ")", remarks: "下发topic")
            column(name: "PROCESS_STATUS", type: "bigint", remarks: "处理流程结果，1: 成功 0:失败") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}