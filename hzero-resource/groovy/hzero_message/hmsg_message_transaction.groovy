package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_message_transaction.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_message_transaction") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_message_transaction_s', startValue:"1")
        }
        createTable(tableName: "hmsg_message_transaction", remarks: "消息事务") {
            column(name: "transaction_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "message_id", type: "bigint",  remarks: "消息ID，hmsg_message.message_id")  {constraints(nullable:"false")}
            column(name: "trx_status_code", type: "varchar(" + 30 * weight + ")",  remarks: "事务状态，值集：HMSG.TRANSACTION_STATUS P:就绪 S:成功 F:失败")  {constraints(nullable:"false")}  
            column(name: "transaction_message", type: "longtext",  remarks: "事务消息")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_message_transaction", indexName: "hmsg_message_transaction_n1") {
            column(name: "message_id")
            column(name: "tenant_id")
        }

    }
	
	changeSet(author: 'hzero@hand-china.com', id: '2020-04-24-hmsg_message_transaction') {
		createIndex(tableName: "hmsg_message", indexName: "hmsg_message_transaction_n2") {
            column(name: "last_update_date")
        }
    }
}