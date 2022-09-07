package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_comment.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_comment") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_comment_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_comment", remarks: "流程任务表处理信息表") {
            column(name: "COMMENT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TASK_ID", type: "bigint", remarks: "流程任务ID，hwkf_run_task主键") { constraints(nullable: "false") }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键") { constraints(nullable: "false") }
            column(name: "COMMENT_CONTENT", type: "varchar(" + 1000 * weight + ")", remarks: "审批意见")
            column(name: "REMARK", type: "varchar(" + 1000 * weight + ")", remarks: "备注")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_run_comment", indexName: "hwkf_run_comment_N3") {
            column(name: "NODE_ID")
        }
        createIndex(tableName: "hwkf_run_comment", indexName: "hwkf_run_comment_N2") {
            column(name: "INSTANCE_ID")
        }
        createIndex(tableName: "hwkf_run_comment", indexName: "hwkf_run_comment_N1") {
            column(name: "TASK_ID")
        }

    }
}