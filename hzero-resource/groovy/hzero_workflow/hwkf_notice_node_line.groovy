package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_notice_node_line.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_notice_node_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_notice_node_line_s', startValue: "1")
        }
        createTable(tableName: "hwkf_notice_node_line", remarks: "工作流通知节点定义") {
            column(name: "NODE_LINE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "NODE_ID", type: "bigint", remarks: "工作流类型ID") { constraints(nullable: "false") }
            column(name: "NOTICE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "通知类型 邮件，短信，微信等") { constraints(nullable: "false") }
            column(name: "NOTICE_TEMPLATE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "Hzero消息发送配置编码") { constraints(nullable: "false") }
            column(name: "SERVER_CODE", type: "varchar(" + 30 * weight + ")", remarks: "通知类型对应的发送账号（emial,sms,webhook,微信公众号，企业微信，语音消息，钉钉）")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "NODE_ID,NOTICE_TYPE,NOTICE_TEMPLATE_CODE", tableName: "hwkf_notice_node_line", constraintName: "hwkf_notice_node_line_u1")
    }
}