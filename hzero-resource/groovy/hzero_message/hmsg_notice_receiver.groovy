package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_notice_receiver.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hmsg_notice_receiver") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_notice_receiver_s', startValue:"1")
        }
        createTable(tableName: "hmsg_notice_receiver", remarks: "公告接收记录") {
            column(name: "receiver_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "published_id", type: "bigint",  remarks: "公告发布记录hmsg_notice_published_record.published_id")   
            column(name: "receiver_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "接收类型")   
            column(name: "receiver_source_id", type: "bigint",  remarks: "接收方ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_notice_receiver", indexName: "hmsg_notice_receiver_n1") {
            column(name: "published_id")
        }

        addUniqueConstraint(columnNames:"published_id,receiver_type_code,receiver_source_id,tenant_id",tableName:"hmsg_notice_receiver",constraintName: "hmsg_notice_receiver_u1")
    }
}