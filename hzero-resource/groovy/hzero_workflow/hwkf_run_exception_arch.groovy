package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_exception_arch.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_exception_arch") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_exception_arch_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_exception_arch", remarks: "流程异常归档表") {
            column(name: "EXCEPTION_ARCH_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "EXCEPTION_ID", type: "bigint", remarks: "流程异常ID，hwkf_run_exception主键") { constraints(nullable: "false") }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "RUN_NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键") { constraints(nullable: "false") }
            column(name: "EXCEPTION_NODE_CODE", type: "varchar(" + 80 * weight + ")", remarks: "")
            column(name: "EXCEPTION_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "执行节点类型 BEFORE_EVENT/AFTER_EVENT/SERVICE/NOTICE") { constraints(nullable: "false") }
            column(name: "EXCEPTION_MESSAGE", type: "longtext", remarks: "错误信息") { constraints(nullable: "false") }
            column(name: "NOTICE_TRANSACTION_ID", type: "bigint", remarks: "hmsg_message_transaction表主键（消息通知事务信息）")
            column(name: "INTERFACE_REQUEST", type: "longtext", remarks: "接口执行请求报文")
            column(name: "RETRY_RESULT", type: "varchar(" + 30 * weight + ")", remarks: "重试结果 SUCCESS/FAIL")
            column(name: "RETRY_TIMES", type: "tinyint", remarks: "重试次数")
            column(name: "RETRY_DATE", type: "datetime", remarks: "上次重试时间")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_run_exception_arch", indexName: "hwkf_run_exception_arch_N1") {
            column(name: "INSTANCE_ID")
        }

        addUniqueConstraint(columnNames: "EXCEPTION_ID", tableName: "hwkf_run_exception_arch", constraintName: "hwkf_run_exception_arch_u1")
    }
}