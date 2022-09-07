package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_document_param_value.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_document_param_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_document_param_value_s', startValue:"1")
        }
        createTable(tableName: "hitf_document_param_value", remarks: "接口文档参数备选值") {
            column(name: "param_value_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "param_id", type: "bigint",  remarks: "参数ID")  {constraints(nullable:"false")}  
            column(name: "document_id", type: "bigint",  remarks: "文档ID")  {constraints(nullable:"false")}  
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否默认")  {constraints(nullable:"false")}  
            column(name: "param_value", type: "varchar(" + 128 * weight + ")",  remarks: "潜在参数值")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hitf_document_param_value", indexName: "hitf_document_param_value_n1") {
            column(name: "param_id")
            column(name: "default_flag")
        }

        addUniqueConstraint(columnNames:"param_id,param_value",tableName:"hitf_document_param_value",constraintName: "hitf_document_param_value_u1")
    }
}