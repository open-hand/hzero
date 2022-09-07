package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_attachment.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_attachment") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_attachment_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_attachment", remarks: "流程附件") {
            column(name: "ATTACHMENT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "ATTACHMENT_UUID", type: "varchar(" + 50 * weight + ")", remarks: "附件集UUID") { constraints(nullable: "false") }
            column(name: "ATTACHMENT_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "附件类型（预留字段）")
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID") { constraints(nullable: "false") }
            column(name: "TASK_ID", type: "bigint", remarks: "执行任务ID") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "INSTANCE_ID,NODE_ID,TASK_ID", tableName: "hwkf_run_attachment", constraintName: "hwkf_run_attachment_u1")
    }
}