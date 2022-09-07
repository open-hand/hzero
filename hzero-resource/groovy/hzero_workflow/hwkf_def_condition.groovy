package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_condition.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_condition") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_condition_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_condition", remarks: "工作流条件定义表") {
            column(name: "CONDITION_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID，HWKF_DEF_TYPE") { constraints(nullable: "false") }
            column(name: "CONDITION_CODE", type: "varchar(" + 40 * weight + ")", remarks: "审批条件编码") { constraints(nullable: "false") }
            column(name: "CONDITION_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批条件名称") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "审批条件描述")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用  1/启用 0/失效") { constraints(nullable: "false") }
            column(name: "WEB_JSON", type: "longtext", remarks: "前端报文")
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
            column(name: "DETAIL_JSON", type: "longtext", remarks: "转换后的详细报文")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TYPE_ID,CONDITION_CODE", tableName: "hwkf_def_condition", constraintName: "hwkf_def_condition_u1")
    }
}