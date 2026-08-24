package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_notice.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hmsg_notice") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_notice_s', startValue:"1")
        }
        createTable(tableName: "hmsg_notice", remarks: "公告基础信息") {
            column(name: "notice_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言code")  {constraints(nullable:"false")}  
            column(name: "title", type: "varchar(" + 300 * weight + ")",  remarks: "公告主题")  {constraints(nullable:"false")}  
            column(name: "receiver_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "接收方类型,值集：hmsg.NOTICE.RECERVER_TYPE")  {constraints(nullable:"false")}  
            column(name: "notice_category_code", type: "varchar(" + 60 * weight + ")",  remarks: "公告类别,值集：hmsg.NOTICE.NOTICE_CATEGORY")  
            column(name: "notice_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "公告类型,值集：hmsg.NOTICE.NOTICE_TYPE")  {constraints(nullable:"false")}  
            column(name: "attachment_uuid", type: "varchar(" + 50 * weight + ")",  remarks: "附件uuid，关联附件表")   
            column(name: "start_date", type: "datetime",  remarks: "有效期从")  {constraints(nullable:"false")}  
            column(name: "end_date", type: "datetime",  remarks: "有效期至")   
            column(name: "published_by", type: "bigint",  remarks: "发布人")   
            column(name: "published_date", type: "datetime",  remarks: "发布时间")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "status_code", type: "varchar(" + 60 * weight + ")",  remarks: "公告状态，值集：hmsg.NOTICE.STATUS")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_notice", indexName: "hmsg_notice_n1") {
            column(name: "notice_type_code")
            column(name: "notice_category_code")
            column(name: "tenant_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-18-hmsg_notice") {
        addColumn(tableName: "hmsg_notice") {
            column(name: "sticky_flag", type: "tinyint",   defaultValue:"0",   remarks: "悬浮公告标识")  {constraints(nullable:"false")}
        }
    }
}