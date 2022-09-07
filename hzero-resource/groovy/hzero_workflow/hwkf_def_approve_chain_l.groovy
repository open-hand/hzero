package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approve_chain_l.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approve_chain_l") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approve_chain_l_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approve_chain_l", remarks: "工作流审批链行") {
            column(name: "CHAIN_LINE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "CHAIN_ID", type: "bigint", remarks: "审批链头ID") { constraints(nullable: "false") }
            column(name: "ORDER_NO", type: "bigint", remarks: "序号") { constraints(nullable: "false") }
            column(name: "PARALLEL_FLAG", type: "int", remarks: "是否并行")
            column(name: "APPROVAL_STRATEGY", type: "varchar(" + 30 * weight + ")", remarks: "审批方式")
            column(name: "PERCENTAGE", type: "varchar(" + 10 * weight + ")", remarks: "比例值")
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "审批链行描述")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "启用") { constraints(nullable: "false") }
            column(name: "PARENT_ID", type: "bigint", remarks: "父ID")
            column(name: "NODE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "节点类型")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "CONDITION_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "条件配置：条件来源类型  PREDEFINED（预设条件）  CUSTOMIZE（自定义条件）")
        }


        addUniqueConstraint(columnNames: "CHAIN_ID,PARENT_ID,ORDER_NO", tableName: "hwkf_def_approve_chain_l", constraintName: "hwkf_def_approve_chain_l_u1")
    }
}