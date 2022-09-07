package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_variable.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_variable") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_variable_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_variable", remarks: "工作流流程变量 （变量关联LOV，作为审批人规则中的审批条件，LOV需要考虑参数）") {
            column(name: "VARIABLE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "VARIABLE_CODE", type: "varchar(" + 40 * weight + ")", remarks: "参数编码") { constraints(nullable: "false") }
            column(name: "VARIABLE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "参数名称") { constraints(nullable: "false") }
            column(name: "VARIABLE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "参数类型") { constraints(nullable: "false") }
            column(name: "SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "参数值来源类型 DEFAULT/自定义，MODEL/数据模型") { constraints(nullable: "false") }
            column(name: "CONDITION_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否审批条件流程变量") { constraints(nullable: "false") }
            column(name: "INTERFACE_ID", type: "bigint", remarks: "如果是条件参数，关联具体的接口的ID")
            column(name: "RANGE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "数据选择来源 CUSTOM/LOV/LIST")
            column(name: "RANGE_LOV_CODE", type: "varchar(" + 60 * weight + ")", remarks: "数据来源CODE：1.为CUSTOM时，手工输入条件右侧值。 2.LOV时，存hpfm_lov.lov_code，选择右侧条件值。 3.LIST时存hpfm_lov_view_header.view_code，选择右侧条件值")
            column(name: "RANGE_NAME", type: "varchar(" + 255 * weight + ")", remarks: "来源名称 LOV名称、接口名称")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用  1/启用 0/失效") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
            column(name: "MODEL_QUERY_FLAG", type: "tinyint", defaultValue: "0", remarks: "模型-模型视图查询值时，必传字段")
            column(name: "APP_CODE", type: "varchar(" + 64 * weight + ")", remarks: "模型-应用编码")
            column(name: "MODEL_VIEW_CODE", type: "varchar(" + 64 * weight + ")", remarks: "模型-数据视图编码")
            column(name: "APP_NAME", type: "varchar(" + 64 * weight + ")", remarks: "模型-应用名称")
            column(name: "SHARED_FLAG", type: "tinyint", defaultValue: "0", remarks: "模型-应用是否是共享应用")
            column(name: "MODEL_VIEW_NAME", type: "varchar(" + 64 * weight + ")", remarks: "模型-数据视图名称")
            column(name: "SESSION_EXPRESSION", type: "varchar(" + 128 * weight + ")", remarks: "会话变量-取值表达式")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,VARIABLE_CODE", tableName: "hwkf_def_variable", constraintName: "hwkf_def_variable_u1")
    }
}