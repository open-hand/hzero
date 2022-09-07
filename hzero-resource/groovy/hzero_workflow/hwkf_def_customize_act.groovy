package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_customize_act.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_customize_act") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_customize_act_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_customize_act", remarks: "工作流自定义审批动作表") {
            column(name: "ACTION_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "ACTION_CODE", type: "varchar(" + 30 * weight + ")", remarks: "动作编码") { constraints(nullable: "false") }
            column(name: "ACTION_NAME", type: "varchar(" + 80 * weight + ")", remarks: "动作名称") { constraints(nullable: "false") }
            column(name: "ACTION_DESC", type: "varchar(" + 240 * weight + ")", remarks: "")
            column(name: "ACTION_RESULT", type: "varchar(" + 30 * weight + ")", remarks: "动作结果，审批后流转方式") { constraints(nullable: "false") }
            column(name: "INTERFACE_ID", type: "bigint", remarks: "接口ID") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源，PREDEFINED(预定义)、CUSTOMIZE(自定义)") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,ACTION_CODE", tableName: "hwkf_def_customize_act", constraintName: "hwkf_def_customize_act_u1")
    }
}