package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_history.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_history") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_history_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_history", remarks: "工作流流程分类历史表") {
            column(name: "HISTORY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "OPERATE_VERSION", type: "bigint", remarks: "操作版本") { constraints(nullable: "false") }
            column(name: "OPERATE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "操作类型 新增、更新、删除") { constraints(nullable: "false") }
            column(name: "OPERATE_TIME", type: "datetime", remarks: "操作时间") { constraints(nullable: "false") }
            column(name: "OPERATE_USER_ID", type: "bigint", remarks: "操作人userId") { constraints(nullable: "false") }
            column(name: "TABLE_NAME", type: "varchar(" + 50 * weight + ")", remarks: "表名称") { constraints(nullable: "false") }
            column(name: "PRIMARY_KEY_ID", type: "bigint", remarks: "表主键ID") { constraints(nullable: "false") }
            column(name: "PRIMARY_CODE", type: "varchar(" + 100 * weight + ")", remarks: "业务编码")
            column(name: "JSON_DETAIL", type: "longtext", remarks: "JSON报文")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }

        createIndex(tableName: "hwkf_def_history", indexName: "HWKF_DEF_HISTORY_N1") {
            column(name: "TABLE_NAME")
            column(name: "PRIMARY_KEY_ID")
        }

    }
}