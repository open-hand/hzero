package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_notice_content.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hmsg_notice_content") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_notice_content_s', startValue:"1")
        }
        createTable(tableName: "hmsg_notice_content", remarks: "公告具体内容") {
            column(name: "notice_content_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "notice_id", type: "bigint",  remarks: "公告头ID")  {constraints(nullable:"false")}  
            column(name: "notice_body", type: "longtext",  remarks: "公告内容")   
            column(name: "attachment_uuid", type: "varchar(" + 50 * weight + ")",  remarks: "附件uuid，关联附件表")   
            column(name: "start_date", type: "datetime",  remarks: "有效期从")   
            column(name: "end_date", type: "datetime",  remarks: "有效期至")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_notice_content", indexName: "hmsg_notice_content_n1") {
            column(name: "notice_id")
            column(name: "tenant_id")
        }

    }
}