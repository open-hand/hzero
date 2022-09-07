package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_form.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_form") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_form_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_form", remarks: "工作流审批表单") {
            column(name: "FORM_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程类型ID") { constraints(nullable: "false") }
            column(name: "FORM_CODE", type: "varchar(" + 30 * weight + ")", remarks: "表单编码") { constraints(nullable: "false") }
            column(name: "FORM_DESCRIPTION", type: "varchar(" + 80 * weight + ")", remarks: "表单描述") { constraints(nullable: "false") }
            column(name: "FORM_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "表单类型 PC/APP") { constraints(nullable: "false") }
            column(name: "FORM_URL", type: "varchar(" + 240 * weight + ")", remarks: "表单URL")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "DEFAULT_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否默认表单，PC端/移动端可各一个默认表单") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
            column(name: "INTERFACE_ID", type: "bigint", remarks: "APP类型表单接口ID")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,FORM_CODE", tableName: "hwkf_def_form", constraintName: "hwkf_def_form_u1")
    }
}