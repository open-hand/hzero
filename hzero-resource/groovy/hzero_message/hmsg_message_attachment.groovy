package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_message_attachment.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_message_attachment") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_message_attachment_s', startValue:"1")
        }
        createTable(tableName: "hmsg_message_attachment", remarks: "消息附件") {
            column(name: "attachment_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "message_id", type: "bigint",  remarks: "消息ID，hmsg_message.message_id")  {constraints(nullable:"false")}  
            column(name: "bucket_name", type: "varchar(" + 60 * weight + ")",  remarks: "附件bucket")  {constraints(nullable:"false")}  
            column(name: "file_url", type: "varchar(" + 120 * weight + ")",  remarks: "附件URL地址")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_message_attachment", indexName: "hmsg_message_attachment_n1") {
            column(name: "message_id")
            column(name: "tenant_id")
        }

    }
}