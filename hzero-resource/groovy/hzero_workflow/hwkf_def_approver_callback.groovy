package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_callback.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approver_callback") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approver_callback_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approver_callback", remarks: "工作流审批人回调表") {
            column(name: "CALLBACK_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "工作流类型ID") { constraints(nullable: "false") }
            column(name: "CALLBACK_PRIMARY_CODE", type: "varchar(" + 30 * weight + ")", remarks: "回调信息唯一编码")
            column(name: "RESULT_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "结果类型 员工、岗位、部门") { constraints(nullable: "false") }
            column(name: "CALLBACK_TYPE", type: "varchar(" + 10 * weight + ")", remarks: "回调类型 SQL、API") { constraints(nullable: "false") }
            column(name: "LOV_CODE", type: "varchar(" + 150 * weight + ")", remarks: "回调值集编码，不是值集视图编码")
            column(name: "LOV_NAME", type: "varchar(" + 150 * weight + ")", remarks: "回调值集名称")
            column(name: "INTERFACE_ID", type: "bigint", remarks: "回调接口ID")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }

        createIndex(tableName: "hwkf_def_approver_callback", indexName: "HWKF_DEF_APPROVER_CALLBACK_N1") {
            column(name: "TYPE_ID")
            column(name: "RESULT_TYPE")
        }

    }
}