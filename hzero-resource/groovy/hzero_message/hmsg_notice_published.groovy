package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_notice_published.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-06-21-hmsg_notice_published") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_notice_published_s', startValue:"1")
        }
        createTable(tableName: "hmsg_notice_published", remarks: "公告发布记录") {
            column(name: "published_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "notice_id", type: "bigint",  remarks: "公告头ID")  {constraints(nullable:"false")}  
            column(name: "notice_body", type: "longtext",  remarks: "公告内容")   
            column(name: "notice_category_code", type: "varchar(" + 60 * weight + ")",  remarks: "公告类别,值集：hmsg.NOTICE.NOTICE_CATEGORY")   
            column(name: "notice_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "公告类型,值集：hmsg.NOTICE.NOTICE_TYPE")   
            column(name: "attachment_uuid", type: "varchar(" + 50 * weight + ")",  remarks: "附件uuid，关联附件表")   
            column(name: "start_date", type: "datetime",  remarks: "有效期从")   
            column(name: "end_date", type: "datetime",  remarks: "有效期至")   
            column(name: "published_status_code", type: "varchar(" + 20 * weight + ")",  remarks: "发布状态")   
            column(name: "published_version", type: "bigint",  remarks: "发布版本")   
            column(name: "published_by", type: "bigint",  remarks: "发布人")   
            column(name: "published_date", type: "datetime",  remarks: "发布时间")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
		createIndex(tableName: "hmsg_notice_published", indexName: "hmsg_notice_published_n1") {
			column(name: "published_status_code")
			column(name: "tenant_id")
		}
	}

    changeSet(author: 'minghui.qiu@hand-china.com', id: '2019-12-23-hmsg_notice_published-add') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_notice_published') {
             column(name: "receiver_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "接收方类型,值集：hmsg.NOTICE.RECERVER_TYPE")
        }
        addColumn(tableName: 'hmsg_notice_published') {
            column(name: "title", type: "varchar(" + 300 * weight + ")",  remarks: "公告主题")
        }
    }
}