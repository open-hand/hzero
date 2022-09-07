package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_document.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_document") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_document_s', startValue:"1")
        }
        createTable(tableName: "hitf_document", remarks: "接口文档") {
            column(name: "document_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "document_name", type: "varchar(" + 128 * weight + ")",  remarks: "文档名称")   
            column(name: "req_mime_type", type: "varchar(" + 80 * weight + ")",  remarks: "请求体MIME类型，代码HITF.MIME_TYPE")   
            column(name: "req_raw_flag", type: "tinyint",  remarks: "请求体是否Raw类型数据")   
            column(name: "req_root_type", type: "varchar(" + 30 * weight + ")",  remarks: "请求体根节点类型，代码HITF.ROOT_TYPE")   
            column(name: "req_demo", type: "longtext",  remarks: "请求示例")   
            column(name: "req_remark", type: "longtext",  remarks: "请求说明")   
            column(name: "resp_mime_type", type: "varchar(" + 80 * weight + ")",  remarks: "响应体MIME类型，代码HITF.MIME_TYPE")   
            column(name: "resp_raw_flag", type: "tinyint",  remarks: "响应体是否Raw类型数据")   
            column(name: "resp_root_type", type: "varchar(" + 30 * weight + ")",  remarks: "响应体根节点类型，代码HITF.ROOT_TYPE")   
            column(name: "resp_success_status", type: "varchar(" + 30 * weight + ")",  remarks: "响应成功状态码")   
            column(name: "resp_success_mime_type", type: "varchar(" + 30 * weight + ")",  remarks: "响应成功MIME类型，代码HITF.MIME_TYPE")   
            column(name: "resp_success_demo", type: "longtext",  remarks: "响应成功示例")   
            column(name: "resp_failed_status", type: "varchar(" + 30 * weight + ")",  remarks: "响应失败状态码")   
            column(name: "resp_failed_mime_type", type: "varchar(" + 30 * weight + ")",  remarks: "响应失败MIME类型，代码HITF.MIME_TYPE")   
            column(name: "resp_failed_demo", type: "longtext",  remarks: "响应失败示例")   
            column(name: "resp_remark", type: "longtext",  remarks: "响应说明")   
            column(name: "remark", type: "longtext",  remarks: "详细说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"interface_id,tenant_id",tableName:"hitf_document",constraintName: "hitf_document_u1")
    }
}