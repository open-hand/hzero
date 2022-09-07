package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_log_msg_up.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_log_msg_up") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_log_msg_up_s', startValue: "1")
        }
        createTable(tableName: "hiot_log_msg_up", remarks: "数据上行日志表") {
            column(name: "UP_LOG_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "GUID", type: "varchar(" + 20 * weight + ")", remarks: "设备或网关的guid")
            column(name: "UP_LOG_UUID", type: "varchar(" + 120 * weight + ")", remarks: "消息处理流程标识符，标识一条记录处理的唯一标识")
            column(name: "SERVICE_INST_IP", type: "varchar(" + 20 * weight + ")", remarks: "消息处理的服务实例IP") { constraints(nullable: "false") }
            column(name: "TOPIC_NAME", type: "varchar(" + 255 * weight + ")", remarks: "消息上报的topic")
            column(name: "PROCESS_STAGE", type: "bigint", remarks: "流程处理阶段(0: 数据接入  1: 数据转换  2: 数据处理)")
            column(name: "PROCESS_STATUS", type: "bigint", defaultValue: "1", remarks: "处理结果（1: 成功 0:失败）") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", defaultValue: "0", remarks: "租户id") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hiot_log_msg_up", indexName: "hiot_uuid_u1") {
            column(name: "UP_LOG_UUID")
        }

    }
}