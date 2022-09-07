package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_orch_task_result.groovy') {

    changeSet(author: "aaron.yi", id: "2020-06-15_horc_orch_task_result") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_orch_task_result_S', startValue: "10001")
        }
        createTable(tableName: "horc_orch_task_result", remarks: "任务响应结果") {
            column(name: "result_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_orch_task_result_PK")
            }
            column(name: "task_instance_id", type: "bigint", remarks: "编排任务实例主键，horc_orch_task_instance.task_id") {
                constraints(nullable: "false")
            }
            column(name: "result", type: "clob", remarks: "响应结果")
            column(name: "process_time", type: "datetime", remarks: "处理时间") {
                constraints(nullable: "false")
            }
            column(name: "text_type", type: "varchar(" + 30 + ")", remarks: "文本类型（JSON/XML/OTHERS）")
            column(name: "content_type", type: "varchar(" + 150 + ")", remarks: "响应内容类型") {
                constraints(nullable: "false")
            }
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
        createIndex(tableName: "horc_orch_task_result", indexName: "horc_orch_task_result_N1") {
            column(name: "process_time")
        }
    }
}