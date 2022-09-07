package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_mail_approve_his.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_mail_approve_his") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_mail_approve_his_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_mail_approve_his", remarks: "工作流邮件审批历史") {
            column(name: "HISTORY_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "PROTOCOL", type: "varchar(" + 30 * weight + ")", remarks: "邮件协议") { constraints(nullable: "false") }
            column(name: "APPROVED_SUBJECT", type: "varchar(" + 30 * weight + ")", remarks: "邮件主题")
            column(name: "APPROVED_BODY", type: "longtext", remarks: "邮件内容")
            column(name: "FROM_MAIL_ACCOUNT", type: "varchar(" + 30 * weight + ")", remarks: "发件人") { constraints(nullable: "false") }
            column(name: "MAIL_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "邮件类型 send/receive")
            column(name: "DISPOSE_STATUS", type: "varchar(" + 30 * weight + ")", remarks: "发送结果 SUCCESS/ERROR")
            column(name: "TASK_ID", type: "bigint", remarks: "任务ID hwkf_run_task") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "PROCESS_INSTANCE_ID", type: "bigint", remarks: "流程实例ID") { constraints(nullable: "false") }
            column(name: "MAIL_ACCOUNT_CODE", type: "varchar(" + 40 * weight + ")", remarks: "邮件服务器邮箱")
            column(name: "ERROR_MESSAGE", type: "longtext", remarks: "错误信息")
            column(name: "MAIL_PROCESS_COUNT", type: "bigint", remarks: "邮件处理总数")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }
}