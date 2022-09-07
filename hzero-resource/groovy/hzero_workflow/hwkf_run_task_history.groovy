package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_task_history.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_task_history") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_task_history_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_task_history", remarks: "历史节点任务表") {
            column(name: "TASK_HISTORY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "DEPLOYMENT_ID", type: "bigint", remarks: "流程定义ID，hwkf_def_deployment主键") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键")
            column(name: "PARENT_NODE_ID", type: "bigint", remarks: "父节点ID")
            column(name: "PARENT_CHAIN_NODE_ID", type: "bigint", remarks: "审批链父nodeId")
            column(name: "NODE_CODE", type: "varchar(" + 80 * weight + ")", remarks: "")
            column(name: "NODE_NAME", type: "varchar(" + 100 * weight + ")", remarks: "活动节点名称")
            column(name: "NODE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "活动节点类型")
            column(name: "HISTORY_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "类型，NODE/TASK") { constraints(nullable: "false") }
            column(name: "ASSIGNEE", type: "varchar(" + 60 * weight + ")", remarks: "任务办理人")
            column(name: "TASK_ID", type: "bigint", remarks: "流程任务ID，hwkf_run_task主键")
            column(name: "START_DATE", type: "datetime", remarks: "开始时间")
            column(name: "END_DATE", type: "datetime", remarks: "结束时间")
            column(name: "STATUS", type: "varchar(" + 30 * weight + ")", remarks: "任务状态，NEWAPPROVEDREJECTED等")
            column(name: "TO_PERSON", type: "varchar(" + 240 * weight + ")", remarks: "转交人/加签人")
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")", remarks: "备注")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "PARENT_TASK_ID", type: "bigint", remarks: "流程父任务ID")
            column(name: "READ_PERSON", type: "varchar(" + 240 * weight + ")", remarks: "已读人员 1 已读/0 未读（暂仅抄送使用）")
            column(name: "SOURCE_GATEWAY", type: "bigint", remarks: "来源网关ID")
            column(name: "BRANCH_TAG", type: "bigint", remarks: "分支标签")
            column(name: "CARBON_COPY_COMMENT", type: "varchar(" + 1200 * weight + ")", remarks: "抄送评论")
            column(name: "SUB_PROCESS_EXECUTE_RULE", type: "varchar(" + 30 * weight + ")", remarks: "子流程节点，执行方式")
            column(name: "USED_CHAIN_ID", type: "bigint", remarks: "使用的chainId")
        }

        createIndex(tableName: "hwkf_run_task_history", indexName: "hwkf_run_task_history_N1") {
            column(name: "INSTANCE_ID")
        }
        createIndex(tableName: "hwkf_run_task_history", indexName: "hwkf_run_task_history_N2") {
            column(name: "DEPLOYMENT_ID")
        }
        createIndex(tableName: "hwkf_run_task_history", indexName: "hwkf_run_task_history_N3") {
            column(name: "NODE_ID")
        }

    }
}