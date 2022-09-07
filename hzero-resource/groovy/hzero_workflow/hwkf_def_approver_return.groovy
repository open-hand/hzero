package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_return.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approver_return") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approver_return_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approver_return", remarks: "工作流审批人规则返回值表") {
            column(name: "RETURN_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "RULE_LINE_ID", type: "bigint", remarks: "规则行ID") { constraints(nullable: "false") }
            column(name: "FIELD_CODE", type: "varchar(" + 30 * weight + ")", remarks: "字段编码") { constraints(nullable: "false") }
            column(name: "FIELD_NAME", type: "varchar(" + 80 * weight + ")", remarks: "字段名称") { constraints(nullable: "false") }
            column(name: "FIELD_WIDTH", type: "bigint", defaultValue: "100", remarks: "字段宽度") { constraints(nullable: "false") }
            column(name: "FIELD_ORDER", type: "bigint", remarks: "字段顺序")
            column(name: "DISPLAY_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否显示字段")
            column(name: "VALUE_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否值字段")
            column(name: "SELECT_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否为查询字段")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用")
            column(name: "LOCKED_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否锁定")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "RULE_LINE_ID,FIELD_CODE", tableName: "hwkf_def_approver_return", constraintName: "hwkf_def_approver_return_u1")
    }
}