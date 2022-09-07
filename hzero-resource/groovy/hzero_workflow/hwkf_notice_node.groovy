package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_notice_node.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_notice_node") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_notice_node_s', startValue: "1")
        }
        createTable(tableName: "hwkf_notice_node", remarks: "工作流通知节点定义") {
            column(name: "NODE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "工作流类型ID") { constraints(nullable: "false") }
            column(name: "NODE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "节点编码") { constraints(nullable: "false") }
            column(name: "NODE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "节点名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,NODE_CODE,TENANT_ID", tableName: "hwkf_notice_node", constraintName: "hwkf_notice_node_u1")
    }
}