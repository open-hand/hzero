package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_flow_document.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_flow_document") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_flow_document_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_flow_document", remarks: "流程定义与业务单据关联表") {
            column(name: "RELATE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "RELATE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "关联编码") { constraints(nullable: "false") }
            column(name: "RELATE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "关联名称") { constraints(nullable: "false") }
            column(name: "COMPANY_NUM", type: "varchar(" + 30 * weight + ")", remarks: "公司编码")
            column(name: "COMPANY_NAME", type: "varchar(" + 240 * weight + ")", remarks: "公司名称")
            column(name: "FLOW_ID", type: "bigint", remarks: "流程定义ID、DEF_WORKFLOW") { constraints(nullable: "false") }
            column(name: "DOCUMENT_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "业务单据类型") { constraints(nullable: "false") }
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")", remarks: "描述")
            column(name: "START_DATE", type: "datetime", remarks: "开始日期")
            column(name: "END_DATE", type: "datetime", remarks: "结束日期")
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "")
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_LOGIN", type: "bigint", defaultValue: "-1", remarks: "")
        }


        addUniqueConstraint(columnNames: "RELATE_CODE,COMPANY_NUM,TENANT_ID", tableName: "hwkf_def_flow_document", constraintName: "hwkf_def_flow_document_u1")
    }
}