package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_expiration_rule.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_expiration_rule") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_expiration_rule_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_expiration_rule", remarks: "工作流超时规则") {
            column(name: "EXPIRATION_RULE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "流程分类编码") { constraints(nullable: "false") }
            column(name: "DEPLOYMENT_ID", type: "bigint", remarks: "关联的部署ID") { constraints(nullable: "false") }
            column(name: "RULE_NODE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "配置超时规则的节点")
            column(name: "RULE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "规则类型 实例规则/节点任务规则") { constraints(nullable: "false") }
            column(name: "RULE_ACTION", type: "varchar(" + 30 * weight + ")", remarks: "规则执行动作 ") { constraints(nullable: "false") }
            column(name: "EXPIRATION_TIME", type: "int", remarks: "超时时间") { constraints(nullable: "false") }
            column(name: "TIME_UNIT", type: "varchar(" + 30 * weight + ")", remarks: "时间单位 小时/天/周") { constraints(nullable: "false") }
            column(name: "ASSIGNEE_RULE_JSON", type: "longtext", remarks: "人员取值规则")
            column(name: "NOTICE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "通知类型")
            column(name: "NOTICE_TEMPLATE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "通知模板")
            column(name: "SERVER_CODE", type: "varchar(" + 30 * weight + ")", remarks: "通知发送账号")
            column(name: "REBUT_TO", type: "varchar(" + 30 * weight + ")", remarks: "驳回至节点")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}