package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_event.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_event") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_event_s', startValue: "10001")
        }
        createTable(tableName: "hebk_event", remarks: "事件") {
            column(name: "event_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_event_pk")
            }
            column(name: "api_version", type: "varchar(" + 20 * weight + ")", remarks: "API版本") {
                constraints(nullable: "false")
            }
            column(name: "service_name", type: "varchar(" + 60 * weight + ")", remarks: "服务名称") {
                constraints(nullable: "false")
            }
            column(name: "code", type: "varchar(" + 30 * weight + ")", remarks: "API操作代码") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "varchar(" + 60 * weight + ")", remarks: "API操作名称") {
                constraints(nullable: "false")
            }
            column(name: "source", type: "varchar(" + 60 * weight + ")", remarks: "处理API请求的服务端") {
                constraints(nullable: "false")
            }
            column(name: "time", type: "datetime", remarks: "API请求的发生时间") {
                constraints(nullable: "false")
            }
            column(name: "request_number", type: "varchar(" + 60 * weight + ")", remarks: "请求流水号")
            column(name: "error_code", type: "varchar(" + 60 * weight + ")", remarks: "错误码")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "hebk_event", indexName: "hebk_event_n1") {
            column(name: "service_name")
        }
        createIndex(tableName: "hebk_event", indexName: "hebk_event_n2") {
            column(name: "code")
        }
        createIndex(tableName: "hebk_event", indexName: "hebk_event_n3") {
            column(name: "time")
        }
        createIndex(tableName: "hebk_event", indexName: "hebk_event_n4") {
            column(name: "request_number")
        }
    }
}