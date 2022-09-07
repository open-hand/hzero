package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approve_chain.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approve_chain") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approve_chain_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approve_chain", remarks: "工作流审批链头") {
            column(name: "CHAIN_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "CHAIN_CODE", type: "varchar(" + 30 * weight + ")", remarks: "审批链编码") { constraints(nullable: "false") }
            column(name: "CHAIN_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批链名称") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "描述")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "启用") { constraints(nullable: "false") }
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "预定义、自定义")
            column(name: "UPGRADE_FROM", type: "bigint", remarks: "从某chainId升级过来")
            column(name: "VERSION", type: "int", defaultValue: "0", remarks: "") { constraints(nullable: "false") }
            column(name: "SOURCE_CHAIN_ID", type: "bigint", remarks: "来源chainId")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "int", defaultValue: "-1", remarks: "")
            column(name: "UPDATED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否被更新")
        }


        addUniqueConstraint(columnNames: "TYPE_ID,CHAIN_CODE,VERSION", tableName: "hwkf_def_approve_chain", constraintName: "hwkf_def_approve_chain_u1")
    }
}