package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_line.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approver_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approver_line_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approver_line", remarks: "工作流审批人规则行表") {
            column(name: "RULE_LINE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "RULE_ID", type: "bigint", remarks: "审批人规则头ID") { constraints(nullable: "false") }
            column(name: "LINE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "审批人规则行编码") { constraints(nullable: "false") }
            column(name: "LINE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批人规则行名称") { constraints(nullable: "false") }
            column(name: "SOURCE_TYPE", type: "varchar(" + 20 * weight + ")", remarks: "来源类型 LOV,INTERFACE,VARIABLE") { constraints(nullable: "false") }
            column(name: "SOURCE_CODE", type: "varchar(" + 80 * weight + ")", remarks: "来源编码 LOV_CODE")
            column(name: "SOURCE_ID", type: "bigint", remarks: "来源ID INTERFACE_ID、VARIABLE_ID")
            column(name: "SOURCE_NAME", type: "varchar(" + 240 * weight + ")", remarks: "来源名称 LOV名称、接口名称、变量名称")
            column(name: "RESULT_TYPE", type: "varchar(" + 80 * weight + ")", remarks: "结果类型 岗位，人，角色，人员组")
            column(name: "HZERO_ORG_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否hzero组织架构")
            column(name: "CALLBACK_ID", type: "bigint", remarks: "回调函数ID")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "RULE_ID,LINE_CODE", tableName: "hwkf_def_approver_line", constraintName: "hwkf_def_approver_line_u1")
    }
}