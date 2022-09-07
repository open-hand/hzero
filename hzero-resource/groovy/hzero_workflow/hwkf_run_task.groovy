package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_task.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_task") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_task_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_task", remarks: "流程任务表") {
            column(name: "TASK_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键") { constraints(nullable: "false") }
            column(name: "TASK_CODE", type: "varchar(" + 80 * weight + ")", remarks: "")
            column(name: "TASK_NAME", type: "varchar(" + 100 * weight + ")", remarks: "任务名称")
            column(name: "PARENT_NODE_ID", type: "bigint", remarks: "父节点ID")
            column(name: "PARENT_TASK_ID", type: "bigint", remarks: "父任务ID")
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "任务描述")
            column(name: "OWNER", type: "varchar(" + 60 * weight + ")", remarks: "任务所有人ID，转办时用到")
            column(name: "ASSIGNEE", type: "varchar(" + 60 * weight + ")", remarks: "任务办理人ID")
            column(name: "PRIORITY", type: "bigint", remarks: "优先级")
            column(name: "START_DATE", type: "datetime", remarks: "开始时间")
            column(name: "TASK_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "任务类型")
            column(name: "SUSPEND_FLAG", type: "tinyint", remarks: "挂起状态")
            column(name: "DELEGATOR", type: "varchar(" + 240 * weight + ")", remarks: "转交人")
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "URGED_FLAG", type: "tinyint", defaultValue: "0", remarks: "催办标志")
        }

        createIndex(tableName: "hwkf_run_task", indexName: "hwkf_run_task_n1") {
            column(name: "INSTANCE_ID")
        }

    }
}