package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_orch_instance.groovy') {

    changeSet(author: "aaron.yi", id: "2020-06-04_horc_orch_instance") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_orch_instance_S', startValue: "10001")
        }
        createTable(tableName: "horc_orch_instance", remarks: "服务编排实例") {
            column(name: "instance_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_orch_instance_PK")
            }
            column(name: "instance_name", type: "varchar(" + 255 * weight + ")", remarks: "编排实例名称") {
                constraints(nullable: "false")
            }
            column(name: "definition_id", type: "bigint", remarks: "编排定义主键，horc_orch_definition.definition_id") {
                constraints(nullable: "false")
            }
            column(name: "statement_type", type: "varchar(" + 30 + ")", remarks: "语句类型，Command type: 0 start workflow, 1 start execution from current node, 2 resume fault-tolerant workflow, 3 resume pause process, 4 start execution from failed node, 5 complement, 6 schedule, 7 rerun, 8 pause, 9 stop, 10 resume waiting thread") {
                constraints(nullable: "false")
            }
            column(name: "instance_json", type: "clob", remarks: "实例配置json") {
                constraints(nullable: "false")
            }
            column(name: "global_param", type: "clob", remarks: "全局参数")
            column(name: "graph_layout", type: "clob", remarks: "编排定义布局") {
                constraints(nullable: "false")
            }
            column(name: "dependency_type", type: "varchar(" + 30 + ")", remarks: "依赖类型（horc.ORCH.STATEMENT_DEP_TYPE）: 0 当前CURRENT, 1 前向FORWARD, 2 后向BACKWORD")
            column(name: "statement_param", type: "clob", remarks: "执行参数")
            column(name: "status_code", type: "varchar(" + 30 + ")", remarks: "编排实例状态（horc.ORCH.INSTANCE_STATUS）: 0 提交成功COMMITTED_SUCCESS, 1 正在RUNNING, 2 准备暂停PREPARING_PAUSE, 3 已暂停PAUSED, 4 准备停止PREPARING_STOP, 5 已停止STOPPED, 6 失败FAILED, 7 已成功SUCCEED, 8 已结束KILLED, 9 等待运行THREAD_WAITTING, 10 等待依赖DEPENDENCY_WAITTING") {
                constraints(nullable: "false")
            }
            column(name: "failover_flag", type: "Tinyint", defaultValue: "0", remarks: "容错标志，0否，1是") {
                constraints(nullable: "false")
            }
            column(name: "failure_strategy", type: "varchar(" + 30 + ")", defaultValue: "CONTINUE", remarks: "失败策略（horc.ORCH.STATEMENT.FAILURE_STRATEGY），0 结束FINISH，1 继续CONTINUE") {
                constraints(nullable: "false")
            }
            column(name: "warning_type", type: "varchar(" + 30 + ")", defaultValue: "NONE", remarks: "预警类型: horc.ORCH.STATEMENT.WARNING_TYPE")
            column(name: "timeout", type: "bigint", defaultValue: "0", remarks: "超时时间") {
                constraints(nullable: "false")
            }
            column(name: "start_time", type: "datetime", remarks: "开始时间") {
                constraints(nullable: "false")
            }
            column(name: "end_time", type: "datetime", remarks: "结束时间")
            column(name: "statement_start_time", type: "datetime", remarks: "句柄执行时间") {
                constraints(nullable: "false")
            }
            column(name: "time_consumption", type: "bigint", remarks: "耗时")
            column(name: "host", type: "varchar(" + 50 + ")", remarks: "主机地址")
            column(name: "execute_path", type: "varchar(" + 255 * weight + ")", remarks: "执行路径")
            column(name: "log_path", type: "varchar(" + 255 * weight + ")", remarks: "日志路径")
            column(name: "alert_flag", type: "Tinyint", defaultValue: "0", remarks: "告警标志。0否，1是") {
                constraints(nullable: "false")
            }
            column(name: "retry_times", type: "int", remarks: "重试次数")
            column(name: "retry_interval", type: "int", remarks: "重试间隔")
            column(name: "instance_priority", type: "varchar(" + 10 + ")", defaultValue: "MEDIUM", remarks: "优先级(horc.ORCH.INSTANCE_PRIORITY): 0 最高HIGHEST,1 HIGH,2 MEDIUM,3 LOW,4 LOWEST") {
                constraints(nullable: "false")
            }
            column(name: "history_statement_type", type: "clob", remarks: "历史执行类型")
            column(name: "worker_group", type: "varchar(" + 30 + ")", defaultValue: "default", remarks: "工作组")
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识,0禁用，1启用") {
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
        addUniqueConstraint(columnNames: "definition_id,instance_name,tenant_id", tableName: "horc_orch_instance", constraintName: "horc_orch_instance_U1")
        createIndex(tableName: "horc_orch_instance", indexName: "horc_orch_instance_N1") {
            column(name: "start_time")
        }
        createIndex(tableName: "horc_orch_instance", indexName: "horc_orch_instance_N2") {
            column(name: "statement_start_time")
        }
    }
}