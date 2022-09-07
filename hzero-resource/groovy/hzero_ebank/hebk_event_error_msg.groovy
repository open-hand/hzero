package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_event_error_msg.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_event_error_msg") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_event_error_msg_s', startValue: "10001")
        }
        createTable(tableName: "hebk_event_error_msg", remarks: "事件错误信息") {
            column(name: "event_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_event_error_msg_pk")
            }
            column(name: "error_message", type: "text", remarks: "错误信息") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-12_hebk_event_error_msg") {
        addColumn(tableName: 'hebk_event_error_msg') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
        }
    }
}
