package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_type_role.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_type_role") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_type_role_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_type_role", remarks: "工作流流程分类角色授权表") {
            column(name: "AUTH_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "ROLE_ID", type: "bigint", remarks: "角色ID,IAM_ROLE") { constraints(nullable: "false") }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID，HWKF_DEF_TYPE") { constraints(nullable: "false") }
            column(name: "START_DATE", type: "date", remarks: "开始日期") { constraints(nullable: "false") }
            column(name: "END_DATE", type: "date", remarks: "结束日期")
            column(name: "TENANT_ID", type: "bigint", remarks: "")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "ROLE_ID,TYPE_ID", tableName: "hwkf_def_type_role", constraintName: "hwkf_def_type_role_u1")
    }
}