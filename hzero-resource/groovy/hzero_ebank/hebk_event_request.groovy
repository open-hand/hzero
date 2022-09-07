package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_event_request.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_event_request") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_event_request_s', startValue: "10001")
        }
        createTable(tableName: "hebk_event_request", remarks: "事件请求") {
            column(name: "event_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_event_request_pk")
            }
            column(name: "request_parameters", type: "text", remarks: "API请求的输入参数")
            column(name: "response_elements", type: "text", remarks: "API响应的数据")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-12_hebk_event_request") {
        addColumn(tableName: 'hebk_event_request') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
        }
    }
}
