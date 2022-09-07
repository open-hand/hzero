package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_document_param.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_document_param") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_document_param_s', startValue:"1")
        }
        createTable(tableName: "hitf_document_param", remarks: "接口文档参数") {
            column(name: "param_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "document_id", type: "bigint",  remarks: "文档ID")  {constraints(nullable:"false")}  
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "action_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"REQ",   remarks: "HTTP操作类型，代码HITF.HTTP_ACTION_TYPE")  {constraints(nullable:"false")}  
            column(name: "mime_type", type: "varchar(" + 80 * weight + ")",  remarks: "MIME类型，代码HITF.MIME_TYPE")   
            column(name: "param_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"GET",   remarks: "参数类型，代码HITF.PARAM_TYPE")   
            column(name: "param_value_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"STRING",   remarks: "参数值类型，代码HITF.PARAM_VALUE_TYPE")  {constraints(nullable:"false")}  
            column(name: "param_name", type: "varchar(" + 128 * weight + ")",  remarks: "参数名")   
            column(name: "default_value", type: "varchar(" + 128 * weight + ")",  remarks: "默认值")   
            column(name: "default_value_longtext", type: "longtext",  remarks: "默认值，LongText")   
            column(name: "required_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否必填")  {constraints(nullable:"false")}  
            column(name: "format_regexp", type: "varchar(" + 30 * weight + ")",  remarks: "格式限制，代码HITF.FORMAT_REGEXP")   
            column(name: "value_demo", type: "longtext",  remarks: "示例")   
            column(name: "parent_id", type: "bigint",   defaultValue:"0",   remarks: "父级ID")   
            column(name: "level_path", type: "varchar(" + 600 * weight + ")",  remarks: "层级")   
            column(name: "order_seq", type: "int",  remarks: "排序号")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_document_param", indexName: "hitf_document_param_n1") {
            column(name: "document_id")
            column(name: "action_type")
            column(name: "param_name")
        }

    }
}