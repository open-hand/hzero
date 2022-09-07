package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_model.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_model") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_model_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_model", remarks: "工作流流程模型表") {
            column(name: "MODEL_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "FLOW_ID", type: "bigint", remarks: "关联流程ID") { constraints(nullable: "false") }
            column(name: "CONFIG_JSON", type: "longtext", remarks: "流程配置json")
            column(name: "DIAGRAM_JSON", type: "longtext", remarks: "流程图Json")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "VERSION", type: "int", defaultValue: "0", remarks: "流程图版本") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "FLOW_ID,VERSION", tableName: "hwkf_def_model", constraintName: "hwkf_def_model_u1")
    }
}