package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_parameter_value.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_parameter_value") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_parameter_value_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_parameter_value", remarks: "工作流流程变量关联参数表") {
            column(name: "PARAMETER_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "SOURCE_TABLE", type: "varchar(" + 32 * weight + ")", remarks: "来源表名称 WFL_DEF_VARIABLE/WFL_DEF_APPROVER_RULE/WFL_DEF_EVENT") { constraints(nullable: "false") }
            column(name: "SOURCE_ID", type: "bigint", remarks: "来源表ID") { constraints(nullable: "false") }
            column(name: "SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "来源类型，DEFAULT/默认类型  RANGE/条件参数范围取值") { constraints(nullable: "false") }
            column(name: "PARAMETER_CODE", type: "varchar(" + 30 * weight + ")", remarks: "参数编码") { constraints(nullable: "false") }
            column(name: "PARAMETER_NAME", type: "varchar(" + 80 * weight + ")", remarks: "参数名称")
            column(name: "PARAMETER_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "接口参数类型")
            column(name: "VALUE_TYPE", type: "varchar(" + 80 * weight + ")", remarks: "参数值来源类型  VARIABLE/CONSTANT") { constraints(nullable: "false") }
            column(name: "VARIABLE_ID", type: "bigint", remarks: "参数值来源于流程变量ID")
            column(name: "DEFAULT_VALUE", type: "varchar(" + 240 * weight + ")", remarks: "默认参数值")
            column(name: "VIEW_CODE", type: "varchar(" + 120 * weight + ")", remarks: "值集视图编码")
            column(name: "VIEW_NAME", type: "varchar(" + 120 * weight + ")", remarks: "值集视图名称")
            column(name: "URL_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "URL类型 PATH/PARAM")
            column(name: "VALUE_MULTI_FLAG", type: "int", remarks: "参数值多选")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_def_parameter_value", indexName: "hwkf_def_parameter_value_n1") {
            column(name: "SOURCE_TABLE")
            column(name: "SOURCE_ID")
            column(name: "PARAMETER_CODE")
            column(name: "SOURCE_TYPE")
        }

    }
}