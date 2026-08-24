package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_sms_filter.groovy') {
    changeSet(author: "hzero", id: "2020-09-07-hmsg_sms_filter") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmsg_sms_filter_s', startValue: "1")
        }
        createTable(tableName: "hmsg_sms_filter", remarks: "短信账户黑白名单") {
            column(name: "sms_filter_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "server_id", type: "bigint", remarks: "短信账户id，hmsg_sms_server.server_id") { constraints(nullable: "false") }
            column(name: "idd", type: "varchar(" + 30 * weight + ")", remarks: "国际冠码") { constraints(nullable: "false") }
            column(name: "phone", type: "varchar(" + 30 * weight + ")", remarks: "手机")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "server_id,idd,phone", tableName: "hmsg_sms_filter", constraintName: "hmsg_sms_filter_u1")
    }
}