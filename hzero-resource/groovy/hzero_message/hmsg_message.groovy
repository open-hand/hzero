package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_message.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_message") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_message_s', startValue:"1")
        }
        createTable(tableName: "hmsg_message", remarks: "消息信息") {
            column(name: "message_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "message_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "消息类型，值集:HMSG.MESSAGE_TYPE M:邮箱 S:短信 W:微信")  {constraints(nullable:"false")}
            column(name: "template_code", type: "varchar(" + 60 * weight + ")",  remarks: "模板代码")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "模板语言")  {constraints(nullable:"false")}
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "账户代码")
            column(name: "subject", type: "varchar(" + 240 * weight + ")",  remarks: "消息标题")
            column(name: "content", type: "longtext",  remarks: "消息内容")
            column(name: "send_flag", type: "tinyint",   defaultValue:"0",   remarks: "发送标记")  {constraints(nullable:"false")}
            column(name: "send_args", type: "varchar(" + 1200 * weight + ")",  remarks: "发送消息的参数，json格式")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "from_tenant_id", type: "bigint",   defaultValue:"0",   remarks: "来源租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
   createIndex(tableName: "hmsg_message", indexName: "hmsg_message_n1") {
            column(name: "send_flag")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-23-hmsg_message") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropDefaultValue(tableName: "hmsg_message", columnName: "from_tenant_id")
        dropColumn(tableName: "hmsg_message", columnName: "from_tenant_id")
        addColumn(tableName: "hmsg_message") {
            column(name: "external_code", type: "varchar(" + 30 * weight + ")",  remarks: "短信非空，外部代码")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-01-hmsg_message") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_message", columnName: 'external_code', newDataType: "varchar(" + 60 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmsg_message") {
        if (helper.isOracle()) {
            sql {
                "ALTER TABLE hmsg_message RENAME COLUMN send_args TO send_args_bak;" +
                        "ALTER TABLE hmsg_message ADD send_args clob;" +
                        "COMMENT ON COLUMN hmsg_message.send_args IS '发送消息的参数，json格式';" +
                        "UPDATE hmsg_message SET send_args = send_args_bak;" +
                        "ALTER TABLE hmsg_message DROP COLUMN send_args_bak;"
            }
        } else {
            modifyDataType(tableName: "hmsg_message", columnName: 'send_args', newDataType: "longtext")
        }
    }
	
	changeSet(author: 'hzero@hand-china.com', id: '2020-04-23-hmsg_message') {
		createIndex(tableName: "hmsg_message", indexName: "hmsg_message_n2") {
            column(name: "message_type_code")
        }
		createIndex(tableName: "hmsg_message", indexName: "hmsg_message_n3") {
            column(name: "creation_date")
        }
    }
	
}