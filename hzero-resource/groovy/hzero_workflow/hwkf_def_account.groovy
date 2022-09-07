package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_account.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_account") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_account_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_account", remarks: "工作流分类默认账号配置") {
            column(name: "ACCOUNT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID hwkf_def_type") { constraints(nullable: "false") }
            column(name: "ACCOUNT_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "账号类型") { constraints(nullable: "false") }
            column(name: "ACCOUNT_SERVER_CODE", type: "varchar(" + 30 * weight + ")", remarks: "默认账号") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,ACCOUNT_TYPE", tableName: "hwkf_def_account", constraintName: "hwkf_def_account_u1")
    }
}