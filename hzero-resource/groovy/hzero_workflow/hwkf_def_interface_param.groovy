package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_interface_param.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_interface_param") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_interface_param_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_interface_param", remarks: "工作流接口参数表") {
            column(name: "PARAMETER_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INTERFACE_ID", type: "bigint", remarks: "接口定义头表ID") { constraints(nullable: "false") }
            column(name: "PARAMETER_CODE", type: "varchar(" + 30 * weight + ")", remarks: "接口参数编码") { constraints(nullable: "false") }
            column(name: "PARAMETER_NAME", type: "varchar(" + 240 * weight + ")", remarks: "接口参数名称") { constraints(nullable: "false") }
            column(name: "PARAMETER_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "接口参数类型") { constraints(nullable: "false") }
            column(name: "URL_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "URL类型 PATH/PARAM") { constraints(nullable: "false") }
            column(name: "DEFAULT_VALUE", type: "varchar(" + 240 * weight + ")", remarks: "默认值")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "INTERFACE_ID,PARAMETER_CODE", tableName: "hwkf_def_interface_param", constraintName: "hwkf_def_interface_param_u")
    }
}