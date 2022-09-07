package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_orch_statement.groovy') {

    changeSet(author: "aaron.yi", id: "2020-06-04_horc_orch_statement") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_orch_statement_S', startValue: "10001")
        }
        createTable(tableName: "horc_orch_statement", remarks: "服务编排执行语句") {
            column(name: "statement_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_orch_statement_PK")
            }
            column(name: "statement_type", type: "varchar(" + 30 + ")", remarks: "语句类型，horc.ORCH.STATEMENT.STATEMENT_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "definition_id", type: "bigint", remarks: "编排定义主键，horc_orch_definition.definition_id") {
                constraints(nullable: "false")
            }
            column(name: "statement_param", type: "clob", remarks: "执行参数")
            column(name: "dependency_type", type: "varchar(" + 30 + ")", remarks: "依赖类型（horc.ORCH.STATEMENT_DEP_TYPE）: 0 当前CURRENT, 1 前向FORWARD, 2 后向BACKWARD")
            column(name: "failure_strategy", type: "varchar(" + 30 + ")", defaultValue: "CONTINUE", remarks: "失败策略（horc.ORCH.STATEMENT.FAILURE_STRATEGY），0 结束FINISH，1 继续CONTINUE") {
                constraints(nullable: "false")
            }
            column(name: "warning_type", type: "varchar(" + 30 + ")", defaultValue: "NONE", remarks: "预警类型: horc.ORCH.STATEMENT.WARNING_TYPE")
            column(name: "schedule_time", type: "datetime", remarks: "调度时间")
            column(name: "start_time", type: "datetime", remarks: "开始时间") {
                constraints(nullable: "false")
            }
            column(name: "dependency", type: "clob", remarks: "依赖")
            column(name: "instance_priority", type: "varchar(" + 10 + ")", defaultValue: "MEDIUM", remarks: "优先级(horc.ORCH.INSTANCE_PRIORITY): 0 最高HIGHEST,1 HIGH,2 MEDIUM,3 LOW,4 LOWEST") {
                constraints(nullable: "false")
            }
            column(name: "worker_group", type: "varchar(" + 50 + ")", defaultValue: "default", remarks: "工作组")
            column(name: "preference", type: "varchar(" + 50 + ")", remarks: "偏好，用于编排任务获取筛选")
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "horc_orch_statement", indexName: "horc_orch_statement_N1") {
            column(name: "start_time")
        }
    }
}