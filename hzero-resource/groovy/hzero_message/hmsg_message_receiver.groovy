package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_message_receiver.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_message_receiver") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_message_receiver_s', startValue:"1")
        }
        createTable(tableName: "hmsg_message_receiver", remarks: "消息接受方") {
            column(name: "receiver_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "message_id", type: "bigint",  remarks: "消息ID，hmsg_message.message_id")  {constraints(nullable:"false")}  
            column(name: "idd", type: "varchar(" + 20 * weight + ")",  remarks: "短信国际冠码")   
            column(name: "receiver_address", type: "varchar(" + 60 * weight + ")",  remarks: "接收方地址，邮箱/手机号/用户ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_message_receiver", indexName: "hmsg_message_receiver_n1") {
            column(name: "message_id")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2019-06-04-hmsg_message_receiver") {
        addColumn(tableName: 'hmsg_message_receiver') {
            column(name: "filter_flag", type: "tinyint",  remarks: "过滤标识")
        }
    }
}