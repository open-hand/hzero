package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_node.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_node") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_node_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_node", remarks: "执行节点表") {
            column(name: "NODE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "DEPLOYMENT_ID", type: "bigint", remarks: "流程定义ID，hwkf_def_deployment主键") { constraints(nullable: "false") }
            column(name: "PARENT_NODE_ID", type: "bigint", remarks: "父执行节点ID")
            column(name: "NODE_CODE", type: "varchar(" + 80 * weight + ")", remarks: "")
            column(name: "NODE_NAME", type: "varchar(" + 100 * weight + ")", remarks: "节点名称")
            column(name: "NODE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "节点类型")
            column(name: "PARENT_CHAIN_NODE_ID", type: "bigint", remarks: "审批链父节点nodeId")
            column(name: "ACTIVE_FLAG", type: "tinyint", remarks: "是否正在执行") { constraints(nullable: "false") }
            column(name: "CONCURRENT_FLAG", type: "tinyint", remarks: "是否并行执行") { constraints(nullable: "false") }
            column(name: "SUSPEND_FLAG", type: "tinyint", remarks: "是否挂起") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "SOURCE_GATEWAY", type: "bigint", remarks: "来源网关ID")
            column(name: "BRANCH_TAG", type: "bigint", remarks: "分支标签")
            column(name: "SUB_PROCESS_EXECUTE_RULE", type: "varchar(" + 30 * weight + ")", remarks: "子流程节点，执行方式")
            column(name: "USED_CHAIN_ID", type: "bigint", remarks: "使用的chainId")
        }

        createIndex(tableName: "hwkf_run_node", indexName: "hwkf_run_node_N2") {
            column(name: "DEPLOYMENT_ID")
        }
        createIndex(tableName: "hwkf_run_node", indexName: "hwkf_run_node_N1") {
            column(name: "INSTANCE_ID")
        }

    }
}